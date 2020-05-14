module Scene

open System
open System.Drawing
open System.IO
open System.Numerics

open FSharp.Data
open FSharp.Data.UnitSystems.SI.UnitSymbols
open JeremyAnsel.Media
open QuantumConcepts.Formats

open BVH
open Constants
open Ray
open Tri

[<Literal>]
let SceneSample = """
[
    {
        "file": "filename.stl",
        "offset": [3.0e-3,0.1e2,1.1e-3],
        "material": "125",
        "number_density_amg": 1000.2e1
    }
]
"""

type SceneFile = JsonProvider<SceneSample, InferTypesFromValues=false>

type Material = { mat:string; number_density:float<amg> }
type Intersection = { distance:float<m>; point:Vector3; dest:(Material * int) option }
type Scene = { intersectScene:Ray -> Intersection option; intersectObj:Ray -> int -> Intersection option; write:unit -> unit; materials:Material [] }

let stlVertToObj (stlVert : StereoLithography.Vertex) : WavefrontObj.ObjVertex =
    WavefrontObj.ObjVertex (stlVert.X, stlVert.Y, stlVert.Z)

let vector3ToObj (v : Vector3) =
    WavefrontObj.ObjVertex (v.X, v.Y, v.Z)

let stlFileToObj (stl : StereoLithography.STLDocument) : WavefrontObj.ObjFile =
    let obj = WavefrontObj.ObjFile ()
    // TODO: avoid exporting duplicate vertices
    Seq.iteri (fun fN (facet : StereoLithography.Facet) ->
        let face = WavefrontObj.ObjFace ()
        Seq.iteri (fun vOff (vertex : StereoLithography.Vertex) ->
            stlVertToObj vertex |> obj.Vertices.Add
            WavefrontObj.ObjTriplet (fN * 3 + vOff + 1,0,0) |> face.Vertices.Add
        ) facet.Vertices
        obj.Faces.Add face
    ) stl.Facets
    obj

let addTextureToModel (model : WavefrontObj.ObjFile) matLibName textureFileName matName =
    model.MaterialLibraries.Add matLibName
    let matMap = WavefrontObj.ObjMaterialMap textureFileName
    let mat = WavefrontObj.ObjMaterial matName
    mat.DiffuseMap <- matMap
    let matFile = WavefrontObj.ObjMaterialFile ()
    matFile.Materials.Add mat
    for face in model.Faces do
        face.MaterialName <- matName
    matFile

type Texture =
    { bitmap:Bitmap
      width:int
      height:int
      getCoords:int -> Vector2*Vector2*Vector2 }

let setTexCoord (texture : Texture) (uv : Vector2) color =
    texture.bitmap.SetPixel (uv.X*(float32 (texture.width-1)) |> int, (1.0f - uv.Y)*(float32 (texture.height-1)) |> int, color)

let addTexture (model : WavefrontObj.ObjFile) (rWidth,rHeight) cols : Texture =
    let numTris = model.Faces.Count
    let numRects = numTris / 2
    // TODO: how should I choose to setup my grid?
    let rows = float numRects / float cols |> ceil |> int
    let texWidth = rWidth * cols
    let texHeight = rHeight * rows

    let texCoords = [|
        for row in 0..rows do
            for col in 0..cols do
                let u = float32 (col*rWidth) / float32 texWidth
                let v = 1.0f - float32 (row*rHeight) / float32 texHeight
                yield Vector2(u,v)
    |]

    let getTexCoordIndices triI =
        let (rectI,o) = Math.DivRem (triI,2)
        let (row,col) = Math.DivRem (rectI,cols)
        let at (r,c) = (cols+1) * r + c
        let ia = at (row,col)
        let ib = if o = 0 then at (row+1,col) else at (row+1,col+1)
        let ic = if o = 0 then at (row+1,col+1) else at (row,col+1)
        // TODO: remove checks if they are slowing things down
        if ia = ib || ia = ic || ib = ic then
            failwith "texture indices are the same"
        if ia < 0 || ib < 0 || ic < 0 then
            failwith "texture indices are negative"
        (ia,ib,ic)

    let getTexCoords triI =
        let (ia,ib,ic) = getTexCoordIndices triI
        (texCoords.[ia], texCoords.[ib], texCoords.[ic])

    let texture : Bitmap = new Bitmap(texWidth, texHeight)

    // printfn "Num tris: %d" numTris
    // printfn "Rows: %d" rows
    // printfn "Cols: %d" cols

    Seq.iter (fun (texCoord : Vector2) ->
        model.TextureVertices.Add(WavefrontObj.ObjVector3(texCoord.X,texCoord.Y))
    ) texCoords

    // set up the texture coords
    Seq.iteri (fun triI (face : WavefrontObj.ObjFace) ->
        let (ia,ib,ic) = getTexCoordIndices triI
        let setTexture vi ti =
            let mutable triplet = face.Vertices.[vi]
            triplet.Texture <- ti+1
            face.Vertices.[vi] <- triplet
        setTexture 0 ia
        setTexture 1 ib
        setTexture 2 ic
    ) model.Faces

    { bitmap=texture; width=texWidth; height=texHeight; getCoords=getTexCoords }

let getUvCoords ((va,vb,vc) : Vector3*Vector3*Vector3) ((ta,tb,tc) : Vector2*Vector2*Vector2) (point : Vector3) : Vector2 =
    if va = vb || va = vc || vb = vc then
        failwith "triangle vertices are the same!"

    // vectors from point to vertices
    let fa = va - point
    let fb = vb - point
    let fc = vc - point
    // areas and factors
    let a = Vector3.Cross(va-vb, va-vc).Length() // main area
    let a1 = Vector3.Cross(fb, fc).Length() / a
    let a2 = Vector3.Cross(fc, fa).Length() / a
    let a3 = Vector3.Cross(fa, fb).Length() / a
    // get final uv coord
    ta * a1 + tb * a2 + tc * a3

let buildTri (vertices : Vector3 array) (face : WavefrontObj.ObjFace) index =
    match List.init 3 (fun i -> vertices.[face.Vertices.[i].Vertex-1]) with
    | [va;vb;vc] -> let tri = (va,vb,vc) in { tri=tri; index=index }
    | _ -> failwith "invalid number of vertices (/=3)"

let vector3FromArray a =
    match a with
    | [|x;y;z|] -> Vector3(x,y,z)
    | _ -> failwith "array has /= 3 values"

let buildScene sceneFileName buildOutputName =
    let baseDir = Path.GetDirectoryName(sceneFileName)
    let sceneFile = SceneFile.Parse(File.ReadAllText(sceneFileName))

    let materials = [| for obj in sceneFile do yield { mat=obj.Material; number_density=obj.NumberDensityAmg * 1.0<amg> } |]

    let vertices',tris' =
        [| for obj in sceneFile do
            let offsetVec = vector3FromArray <| Array.map float32 obj.Offset

            let objModel = File.OpenRead (Path.Combine(baseDir,obj.File)) |> StereoLithography.STLDocument.Read |> stlFileToObj

            let objVertices = [| for v in objModel.Vertices do yield offsetVec + Vector3(v.Position.X,v.Position.Y,v.Position.Z) |]
            let objTris = [| for face,i in Seq.zip objModel.Faces {0..objModel.Faces.Count} do yield (buildTri objVertices face i,face) |]

            yield (objVertices,objTris)
        |] |> Array.unzip

    let objFile = WavefrontObj.ObjFile()

    for tris,verticesBefore in Seq.zip tris' (Array.scan (fun totalLen (verts : Vector3 []) -> totalLen + verts.Length) 0 vertices') do
        for _,face in tris do
            // printfn "%A" face.Vertices
            for i in {0..face.Vertices.Count-1} do
                let mutable newTriplet = face.Vertices.[i]
                // newTriplet.Normal <- newTriplet.Normal + len
                newTriplet.Vertex <- newTriplet.Vertex + verticesBefore
                // newTriplet.Texture <- newTriplet.Texture + len
                face.Vertices.[i] <- newTriplet

            // printfn "%A" face.Vertices
            objFile.Faces.Add face

    let dropLast (a : 'a []) = Array.take (a.Length - 1) a

    let vertices = Array.concat vertices'
    let triCounts = Array.scan (fun totalTris (tris : (Tri * WavefrontObj.ObjFace) []) -> totalTris + tris.Length) 0 tris'
    let aOfTris = Array.zip tris' (dropLast triCounts)
                |> Array.map (fun (tris,trisBefore) -> Array.map (fun ({ tri=tri; index=index },_) -> { tri=tri; index=index+trisBefore }) tris)
    let tris = Array.concat aOfTris

    for vertex in vertices do
        objFile.Vertices.Add <| vector3ToObj vertex

    // printfn "total # of vertices: %d" vertices.Length
    // printfn "total # of tris: %d" tris.Length

    let bvh = buildBvh tris
    let bvhs = Array.map buildBvh aOfTris

    // TODO: pass dims in on creation?
    let texture = addTexture objFile (10,10) 1000

    let textureFileName = buildOutputName ".bmp"

    let matLibName = buildOutputName ".mtl"
    let matFile = addTextureToModel objFile (Path.GetFileName(matLibName)) (Path.GetFileName(textureFileName)) "textured_material"

    let getHitObj i =
        let index = Array.BinarySearch (triCounts, i)
        if index >= 0 then
            index
        else
            let upperIndex = ~~~ index
            if upperIndex <= 0 || upperIndex >= tris.Length then
                failwith "got out-of-bounds index from intersectBvh"
            else
                upperIndex - 1 // we are in the bucket below this

    let mutable insideTotal = 0
    let mutable insideFailures = 0

    {
        intersectScene=(fun ray ->
            intersectBvh ray bvh |>
            Option.map (fun (dist,i) ->
                let hitObj = getHitObj i
                let point = ray.PointOn dist
                let uv = getUvCoords tris.[i].tri (texture.getCoords i) point
                setTexCoord texture uv Color.White
                // TODO: is the model in cm? also, Vector3s can't have units (to my knowledge)
                { distance=float dist * 1.0<m>; point=point + ray.dir*epsilon; dest=Some (materials.[hitObj], hitObj) }
            )
        );
        intersectObj=(fun ray obj ->
            let intersection = intersectBvh ray bvhs.[obj]
            if intersection.IsNone then
                insideFailures <- insideFailures + 1
            insideTotal <- insideTotal + 1
            Option.map (fun (dist,i) ->
                let point = ray.PointOn dist
                let uv = getUvCoords tris.[i].tri (texture.getCoords i) point
                setTexCoord texture uv Color.Blue
                { distance=float dist * 1.0<m>; point = point + ray.dir*epsilon; dest=None }
            ) intersection
        );
        write=(fun () ->
            printfn "failed to find an intersection %0.2f%% of the time inside objects" (100.0 * float insideFailures/float insideTotal)

            File.OpenWrite textureFileName |> fun stream -> texture.bitmap.Save (stream,Imaging.ImageFormat.Bmp)
            texture.bitmap.Dispose()

            File.OpenWrite matLibName |> matFile.WriteTo
            File.OpenWrite (buildOutputName ".obj") |> objFile.WriteTo
        );
        materials=materials
    }
