+++
title = "Efficient and Robust Quadratures for Isogeometric Analysis: Reduced Gauss and Gauss-Greville rules"
description = "Z. Zou, T.J.R Hughes, M.A. Scott, Di Miao, and R.A. Sauer"
date = "2021-08-16"
research_doc = "manuscript.pdf"
+++

This work proposes two efficient quadrature rules, reduced Gauss quadrature and Gauss-
Greville quadrature, for B-spline and NURBS based isogeometric analysis. The rules are con-
structed to exactly integrate one-dimensional B-spline basis functions of degree $p$, and continuity
class $C^{p−k}$, where $k$ is the highest order of derivatives appearing in the Galerkin formulation
of the problem under consideration. This is the same idea we utilized in [1], but the rules
therein produced negative weights for certain non-uniform meshes. The present work improves
upon [1] in that the weights are guaranteed to be positive for all meshes. The reduced Gauss
quadrature rule is built element-wise according to the element basis degree and smoothness.
The Gauss-Greville quadrature rule combines the proposed reduced Gauss quadrature and Gre-
ville quadrature [1]. Both quadrature rules involve many fewer quadrature points than the full
Gauss quadrature rule and avoid negative quadrature weights for arbitrary knot vectors. The
proposed quadrature rules are stable and accurate, and they can be constructed without solv-
ing nonlinear equations, therefore providing efficient and easy-to-use alternatives to full Gauss
quadrature. Various numerical examples, including curved shells, demonstrate that they achieve
good accuracy, and for $p = 5$ and $6$ eliminate locking.
