$ Sculpt lecture 21. Exercise 9

BEGIN SCULPT

  $ input file conatins volume fractions of 20 materials
  input_micro = micro.tec
  
  $ exodus mesh output file
  exodus_file = micro_clipped

  $ specify a clipping boundary so we don't generate the full mesh
  xmin = 52.145432
  ymin = 87.530418
  zmin = 39.749386
  xmax = 73.055649
  ymax = 150.00000
  zmax = 63.183693

  $ expand the boundaries to improve quality 
  micro_expand = 1
  
  $ turn on pillowing
  pillow = 1

  $ filter boundary cells to avoid single isolated cells at the RVE surface  
  micro_shave = true 
  
END SCULPT