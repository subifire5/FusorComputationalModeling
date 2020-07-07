import numpy as np

# cache stl files
stls = {}

class STL:
    def __init__(self, path=None, pair=None):
        if path:
            self.read(path)
        elif pair:
            normals,facets = pair
            self.normals = np.array(normals).reshape(-1,3)
            self.facets = np.array(facets).reshape(-1,3,3)
        else:
            raise ValueError('no arguments to STL constructor')

        self.vertices = self.facets.view().reshape(-1,3)

        self.minv = self.min(axis=0)
        self.maxv = self.max(axis=0)
        self.aabb = (self.minv,self.maxv)
    
    def read(self, path):
        if path in stls:
            self.normals,self.facets = map(lambda a: a.copy(), stls[path])
        else:
            normals,facets = [],[]
            with open(path) as stl_file:
                stl_lines = stl_file.readlines()
                for i in range(1,len(stl_lines)-1,7):
                    facet = stl_lines[i:i+7]
                    normal,*vertices = (map(float,facet[i].split()[-3:]) for i in [0,2,3,4])
                    normals.extend(normal)
                    for vertex in vertices:
                        facets.extend(vertex)
            self.normals = np.array(normals).reshape(-1,3)
            self.facets = np.array(facets).reshape(-1,3,3)
            stls[path] = (self.normals.copy(),self.facets.copy())
    
    def write(self, path):
        # TODO: could be cleaner
        def show_v3(v):
            # TODO: use scientific notation?
            return ' '.join(map(str,v))

        def indent(strs):
            return [ '\t'+s for s in strs ]

        with open(path, 'w') as stl_file:
            stl_file.write(f'solid "{path}"\n')
            for normal,facet in zip(self.normals,self.facets):
                stl_file.write(
                    '\n'.join(indent(
                        [ f'facet normal {show_v3(normal)}']
                        + indent(
                            ['outer loop']
                            + indent([ f'vertex {show_v3(vertex)}' for vertex in facet ]) +
                            ['endloop']
                        ) +
                        [f'endfacet']
                    )) + '\n'
                )
            stl_file.write(f'endsolid "{path}"\n')
    
    # delegate all unrecognized methods to the vertices
    def __getattr__(self, method_name):
        return getattr(self.vertices,method_name)
