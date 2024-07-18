#!python
import cubit
from math import *

# define the parametric values
sides = 6
radius = 10
distance = 20

# find the angles of the n-gon
delta_angle = 2*pi/sides
angle = 0

# create the vertices
for i in range(sides):
   x = radius*cos(angle)
   y = radius*sin(angle)
   command = "create vertex " + str(x) + " " + str(y)
   cubit.cmd(command)
   angle = angle + delta_angle

# make the sides
for i in range(1,sides):
   command =  "create curve vertex " + str(i) + " " + str(i+1)
   cubit.cmd(command)

# close the n-gon
command =  "create curve vertex " + str(sides) + " " + str(1)
cubit.cmd(command)

# create a surface
cubit.cmd("create surf curve all")

# create a volume
cubit.cmd("sweep surf 1 perpendicular distance " + str(distance))


