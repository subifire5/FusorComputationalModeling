import os.path
import json

from STL import STL

def read_scene(scene_filename):
    scene_dir = os.path.dirname(scene_filename)
    with open(scene_filename) as scene_file:
        scene = json.load(scene_file)
        for obj in scene:
            stl = STL(os.path.join(scene_dir, obj['file']))
            stl.vertices += obj['offset']
            yield (obj,stl)
