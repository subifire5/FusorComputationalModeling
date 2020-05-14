module BVH

open System.Numerics

open AABB
open Tri

type BVHStructure =
    | BVHNode of AABB * BVHStructure array
    | BVHLeaf of AABB * Tri list // TODO: should this be an int array?

type Octree =
    | OctreeDummy of AABB
    | OctreeNode of AABB * Octree array
    | OctreeLeaf of AABB * int list

let getOctAabb octree =
    match octree with
    | OctreeDummy aabb -> aabb
    | OctreeNode (aabb,_) -> aabb
    | OctreeLeaf (aabb,_) -> aabb

let getBvhAabb bvh =
    match bvh with
    | BVHNode (aabb,_) -> aabb
    | BVHLeaf (aabb,_) -> aabb

let octSplitAabb { min=min; max=max} =
    let center = (min + max) / 2.0f
    [|
        { min=Vector3(min.X,min.Y,min.Z); max=Vector3(center.X,center.Y,center.Z) }
        { min=Vector3(center.X,min.Y,min.Z); max=Vector3(max.X,center.Y,center.Z) }
        { min=Vector3(min.X,center.Y,min.Z); max=Vector3(center.X,max.Y,center.Z) }
        { min=Vector3(center.X,center.Y,min.Z); max=Vector3(max.X,max.Y,center.Z) }

        { min=Vector3(min.X,min.Y,center.Z); max=Vector3(center.X,center.Y,max.Z) }
        { min=Vector3(center.X,min.Y,center.Z); max=Vector3(max.X,center.Y,max.Z) }
        { min=Vector3(min.X,center.Y,center.Z); max=Vector3(center.X,max.Y,max.Z) }
        { min=Vector3(center.X,center.Y,center.Z); max=Vector3(max.X,max.Y,max.Z) }
    |]

let createEmptyTree aabb =
    let octs = octSplitAabb aabb |> Array.map OctreeDummy
    OctreeNode (aabb,octs)

let isDummy octree =
    match octree with
    | OctreeDummy _ -> true
    | _ -> false

let buildBvh tris =
    let centroids = Array.map getCentroid tris
    let maxDepth = 16 // TODO: fine-tune

    let insert octree index =
        let rec insert' octree index depth =
            match octree with
            | OctreeNode (aabb,children) ->
                let newChildren = Array.map (fun child -> if containsPoint (getOctAabb child) centroids.[index] then insert' child index (depth+1) else child) children
                OctreeNode (aabb,newChildren)
            | OctreeLeaf (aabb,indices) ->
                let newIndices = index :: indices
                if depth < maxDepth then
                    List.fold (fun tree i -> insert' tree i (depth+1)) (createEmptyTree aabb) newIndices
                else OctreeLeaf (aabb,newIndices)
            | OctreeDummy aabb -> OctreeLeaf (aabb,[index])
        insert' octree index 0

    let (aabb,aabbs) = getAabb tris
    let octree = List.fold insert (createEmptyTree aabb) [0..tris.Length - 1]

    let rec buildBvhFromOctree octree =
        match octree with
        | OctreeNode (_,children) ->
            let bvhs = Array.filter (isDummy >> not) children |> Array.map buildBvhFromOctree
            let newAabb = Array.map getBvhAabb bvhs |> Array.reduce (+)
            BVHNode (newAabb,bvhs)
        | OctreeLeaf (_,indices) ->
            let newAabb = List.map (fun i -> aabbs.[i]) indices |> List.reduce (+)
            let newTris = List.map (fun i -> tris.[i]) indices
            BVHLeaf (newAabb,newTris)
        | _ -> failwith "found neither octree node or leaf in BVH construction"

    buildBvhFromOctree octree

let rec intersectBvh ray bvh =
    match bvh with
    | BVHNode (aabb,children) ->
        if intersectsAabb ray aabb then
            match Array.choose (intersectBvh ray) children with
            | [||] -> None
            | arr -> Array.minBy fst arr |> Some
        else None
    | BVHLeaf (aabb,tris) ->
        if intersectsAabb ray aabb then
            match List.choose (intersectTri ray) tris with
            | [] -> None
            | lst -> List.minBy fst lst |> Some
        else None
