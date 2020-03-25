module Tri

open System.Numerics

open AABB
open Constants
open Ray

type Tri = { tri:Vector3*Vector3*Vector3; index:int }

let intersectTri { o=o; dir=dir } { tri=(va,vb,vc); index=index } =
    let ab = vb - va
    let ac = vc - va
    let pvec = Vector3.Cross(dir, ac)
    let det = Vector3.Dot(ab, pvec)
    let invDet = 1.0f / det
    let tvec = o - va
    let u = Vector3.Dot(tvec, pvec) * invDet
    let qvec = Vector3.Cross(tvec, ab)
    let v = Vector3.Dot(dir, qvec) * invDet
    let t = Vector3.Dot(ac, qvec) * invDet
    if abs det < epsilon then
        None
    elif u < 0.0f || u > 1.0f then
        None
    elif v < 0.0f || u + v > 1.0f then
        None
    elif t < 0.0f then
        None
    else
        Some (t,index)
    
let buildAabbTri { tri=(va,vb,vc) } =
    { min=Vector3.Min(va,Vector3.Min(vb,vc)) - epsilonVec; max=Vector3.Max(va,Vector3.Max(vb,vc)) + epsilonVec }

let getCentroid { tri=(a,b,c) } = (a + b + c) / 3.0f

let intersectTris tris ray =
    match Array.choose (intersectTri ray) tris with
    | [||] -> None
    | arr -> Array.minBy fst arr |> Some

let getAabb tris =
    let aabbs = Array.map buildAabbTri tris
    (Array.reduce (+) aabbs, aabbs)
