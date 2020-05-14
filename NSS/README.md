# neutron-diffusion-is

## Requirements
* .NET (https://dotnet.microsoft.com/download)
* Python 3.6+ (optional; for processing cross section and scene files) (https://www.python.org/downloads/)
  * PyNE (for reading ENDF files) (https://pyne.io/install/index.html - disclaimer: I haven't installed this on Windows)

## Usage
* Open a prompt in the project directory
* To run a simulation, run `dotnet run -p NeutronDiffusion --scene SCENE_FILE --output OUTPUT_NAME NUMBER_OF_NEUTRONS`
* The program should output:
  * OUTPUT_NAME.obj: textured 3D model of results, white is entrance, blue is exit
  * OUTPUT_NAME.log: a csv log of every event in the simulation
  * Some other stuff that doesn't matter

TODO: describe scene json format and the Python tools
