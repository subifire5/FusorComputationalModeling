module Constants

open System.Numerics

open FSharp.Data.UnitSystems.SI.UnitSymbols

let epsilon = 1e-3f
let epsilonVec = Vector3.One * epsilon;

[<Measure>] type eV // electron volts
[<Measure>] type barn // barns
[<Measure>] type amg // Amagats

let JoulesPerEV = 1.6021766208E-19<J/eV>
let M2PerBarn = 1e-28<m^2/barn>
let InvM3PerAmg = 2.6867811e25<m^-3/amg>

let boltzmann = 8.61733333353e-5<eV/K>
let roomTemp = 293.0<K>
let neutronMass = 1.008664f // u
let protonMass = 1.007276f // u
