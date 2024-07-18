+++
title = "U-splines: splines over unstructured meshes"
description = "Derek C. Thomas, Luke Engvall, Steven K. Schmidt, Kevin Tew, Michael A. Scott"
date = "2022-11-11"
research_doc = "usplines2022.pdf"
+++

U-splines are a novel approach to the construction of a spline basis for representing smooth objects in Computer-
Aided Design (CAD) and Computer-Aided Engineering (CAE). A spline is a piecewise-defined function that
satisfies continuity constraints between adjacent elements in a mesh. U-splines differ from existing spline constructions,
such as Non-Uniform Rational B- splines (NURBS), subdivision surfaces, T-splines, and hierarchical
B-splines, in that they can accommodate local geometrically exact adaptivity in h (element size), p (polynomial
degree), and k (smoothness) simultaneously over more varied mesh topology. U-splines have no restrictions on
the placement of T-junctions in the mesh. Mixed element meshes (e.g., triangle and quadrilateral elements in
the same surface mesh) are also supported. We conjecture that the U-spline basis is positive, forms a partition
of unity, is linearly independent, and provides optimal approximation when used in analysis.
