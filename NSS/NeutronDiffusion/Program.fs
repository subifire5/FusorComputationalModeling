open System
open System.IO
open System.Numerics

open FSharp.Data.UnitSystems.SI.UnitSymbols // lets me see unit symbols in editor
open MathNet.Numerics.Distributions

open Arguments
open Constants
open CrossSections
open Scene

let rnd = System.Random ()
let normalRnd = Normal ()

let randomDir () =
    // TODO: is this generator biased?
    let theta = rnd.NextDouble() * 2.0 * Math.PI |> float32
    let phi = rnd.NextDouble() * 2.0 - 1.0 |> acos |> float32
    Vector3 (cos theta * sin phi, sin theta * sin phi, cos phi)

let pathLength (sigma : float</m>) () : float<m> =
    (-1.0 / sigma) * log (rnd.NextDouble())

let elasticScatter (neutronDir : Vector3) (neutronEnergy : float<eV>) =
    let protonEnergy = normalRnd.Sample() * (boltzmann * roomTemp / 2.0)
    let protonDir = randomDir()

    let neutron = (float32 neutronEnergy) * neutronDir
    let proton = (float32 protonEnergy) * protonDir

    let referenceFrame = (neutronMass * neutron + proton * protonMass) * (1.0f / (neutronMass + protonMass))
    let neutronRef = neutron - referenceFrame
    let scatteredRef = neutronRef.Length() * randomDir()
    let scattered = scatteredRef + referenceFrame

    Vector3.Normalize(scattered), 1.0<eV> * float (scattered.Length())

let showV (v3 : Vector3) =
    sprintf "%f %f %f" v3.X v3.Y v3.Z

let startingEnergy = 2.45e6<eV> // is this the starting energy?

[<EntryPoint>]
let main argv =
    let settings = readSettings argv

    if not (Directory.Exists(settings.outputDir)) then
        printfn "creating directory %s for output" settings.outputDir
        Directory.CreateDirectory(settings.outputDir) |> ignore

    let buildOutputName suffix = Path.Combine(settings.outputDir,settings.outputName + suffix)

    printfn "Building scene..."
    let scene = buildScene settings.sceneFile buildOutputName
    printfn "...done"

    let crossSectionsMap : Map<string,CrossSection> =
        Seq.map (fun material -> material.mat) scene.materials
        |> Seq.distinct
        |> Seq.map (fun mat -> mat,readCrossSections mat)
        |> Map.ofSeq

    // printfn "cross sections map: %A" crossSectionsMap

    let mutable numScattered = 0
    let mutable numEscaped = 0
    let mutable numAbsorbed = 0
    let mutable numEscapedFromObj = 0

    use logFile = new StreamWriter(buildOutputName ".log")
    // Event IDs: 1 = Elastic Scattering, 2 = Absorption, 3 = Escape, 4 = Escape Obj (Error)
    fprintfn logFile "Neutron #,Event #,Event ID,Pos,In Dir,Out Dir,In Energy (eV),Out Energy (eV)"
    let logLine = fprintfn logFile "%d,%d,%d,%s,%s,%s,%s,%s"

    let escape num eventNum pos dir energy =
        numEscaped <- numEscaped + 1
        logLine num eventNum 3 (showV pos) (showV dir) "NA" (string energy) "NA"

    let escapeObj num eventNum pos dir energy =
        numEscapedFromObj <- numEscapedFromObj + 1
        logLine num eventNum 4 (showV pos) (showV dir) "NA" (string energy) "NA"

    let recordElasticScatter num eventNum pos dir newDir energy newEnergy =
        numScattered <- numScattered + 1
        logLine num eventNum 1 (showV pos) (showV dir) (showV newDir) (string energy) (string newEnergy)

    let absorb num eventNum pos dir energy =
        numAbsorbed <- numAbsorbed + 1
        logLine num eventNum 2 (showV pos) (showV dir) "NA" (string energy) "NA"

    let rec scatter num eventNum (pos : Vector3) (dir : Vector3) (energy : float<eV>) (inside : (Material * int) option) =
        let intersection = match inside with
                           | None -> scene.intersectScene { o=pos; dir=dir }
                           | Some (mat,obj) -> scene.intersectObj { o=pos; dir=dir } obj
        match intersection with
        | None -> if inside.IsNone then escape num eventNum pos dir energy else escapeObj num eventNum pos dir energy
        | Some { point=intPos; distance=distance; dest=dest } ->
            match inside with
            | None -> scatter num eventNum intPos dir energy dest
            | Some (mat,obj) ->
                let crossSections = (crossSectionsMap.TryFind mat.mat).Value
                let totalMicro = (crossSections.getTotal energy).Value
                let totalMacro = totalMicro * InvM3PerAmg * mat.number_density * M2PerBarn
                let distanceTraveled = pathLength totalMacro ()
                if distanceTraveled < distance then // we are doing something!
                    let elasticMicro = (crossSections.getElastic energy).Value
                    let elasticMacro = elasticMicro * InvM3PerAmg * mat.number_density * M2PerBarn
                    if (elasticMacro / totalMacro) > rnd.NextDouble() then // elastic scattering
                        let newDir,newEnergy = elasticScatter dir energy
                        recordElasticScatter num eventNum pos dir newDir energy newEnergy
                        scatter num (eventNum + 1) (pos + dir * (float32 distanceTraveled)) newDir newEnergy inside // inside not dest because we didn't leave
                    else // absorbed
                        absorb num eventNum pos dir energy
                else // just keep going
                    scatter num eventNum intPos dir energy dest

    printfn "Scattering..."
    for neutronNum in [1..settings.neutrons] do
        scatter neutronNum 0 Vector3.Zero (randomDir()) startingEnergy None

    printfn "...done"

    printfn "Writing scene..."
    scene.write()
    printfn "...done"

    printfn "%d neutrons scattered" numScattered
    printfn "%d neutrons escaped" numEscaped
    printfn "%d neutrons were absorbed" numAbsorbed
    printfn "%d neutrons escaped from objects (bad)" numEscapedFromObj

    0
