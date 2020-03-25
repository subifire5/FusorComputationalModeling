module Ray

open System.Numerics

type Ray = { o:Vector3; dir:Vector3 }
    with member this.PointOn (t : float32) = this.o + this.dir * t
