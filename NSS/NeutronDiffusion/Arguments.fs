module Arguments

open System

open Argu

[<NoAppSettings>]
type Arguments =
    | [<Mandatory; ExactlyOnce>] Scene of scene_file:string
    | [<Mandatory; ExactlyOnce>] Output of output_name:string
    | OutputDir of output_dir:string
    | [<MainCommand; ExactlyOnce; Last>] Neutrons of num_neutrons:int
with
    interface IArgParserTemplate with
        member arg.Usage =
            match arg with
            | Scene _ -> "path to JSON file describing scene"
            | Output _ -> "name to give output files"
            | OutputDir _ -> "directory to write output files to (defaults to output)"
            | Neutrons _ -> "# of neutrons to simulate"

type Settings = { neutrons:int; sceneFile:string; outputName:string; outputDir:string  }

let readSettings argv =
    let errorHandler = ProcessExiter(colorizer = function ErrorCode.HelpText -> None | _ -> Some ConsoleColor.Red)
    let parser = ArgumentParser.Create<Arguments>(programName = "diffuser", errorHandler = errorHandler)
    let results = parser.ParseCommandLine argv

    { neutrons=results.GetResult(Neutrons); sceneFile=results.GetResult(Scene); outputName=results.GetResult(Output); outputDir=results.GetResult(OutputDir, defaultValue = "output") }