import os.path
import sys

from STL import STL
import scene

scene_filename = sys.argv[1]
scene_basename = os.path.basename(scene_filename)
scene_pairs = []
for _,stl in scene.read_scene(scene_filename):
    scene_pairs.extend(zip(stl.normals,stl.facets))

scene_stl = STL(pair=zip(*scene_pairs))
scene_stl.write(f'{os.path.splitext(scene_basename)[0]}.stl')
