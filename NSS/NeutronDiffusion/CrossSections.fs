module CrossSections

open System
open System.IO

open Constants

// is having partially applied lookups here a good idea?
// should this return barns?
type CrossSection = { getTotal:float<eV> -> float<barn> option; getElastic:float<eV> -> float<barn> option}

let linearInterpolate (x0,y0) (x1,y1) x2 =
    (x0*y1 - x1*y0 + x2*(y0 - y1)) / (x0 - x1)

let calcCrossSection (pairs : array<float<eV> * float<barn>>) (energy : float<eV>) =
    let energies = Array.map fst pairs // TODO: does this cost me?
    let index = Array.BinarySearch (energies, energy)
    if index >= 0 then
        Some <| snd pairs.[index]
    else
        let upperIndex = ~~~ index
        if upperIndex <= 0 || upperIndex >= pairs.Length then
            None
        else
            Some <| linearInterpolate pairs.[upperIndex-1] pairs.[upperIndex] energy

let readCrossSection material typ =
    let readLine (line : string) = match line.Split [|','|] with
                                    | [|energy;crossSection|] -> (float energy) * 1.0<eV>,(float crossSection) * 1.0<barn>
                                    | _ -> failwith "pointwise csv has more than two fields"
    let pairs = "cross_sections/csv/" + typ + "/" + material |> File.ReadLines |> Seq.toArray |> Array.map readLine
    calcCrossSection pairs

let readCrossSections material =
    { getTotal=readCrossSection material "total"; getElastic=readCrossSection material "elastic" }
