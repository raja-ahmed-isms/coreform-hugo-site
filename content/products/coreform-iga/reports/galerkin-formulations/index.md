+++
title = "Galerkin formulations of isogeometric shell analysis: Alleviating locking with Greville quadratures and higher-order elements"
description = "Z. Zou, T.J.R Hughes, M.A. Scott, R.A. Sauer, and E.J. Savitha"
date = "2021-01-06"
research_doc = "reducedintegration.pdf"
+++

 We propose new quadrature schemes that asymptotically require only four in-plane points for Reissner-Mindlin shell elements and nine in-plane points for Kirchhoff-Love shell elements in B-spline and NURBS-based isogeometric shell analysis, independent of the polynomial degree $p$ of the elements. The quadrature points are Greville abscissae associated with $p$th-order B-spline basis functions whose continuities depends on the specific Galerkin formulations, and the quadrature weights are calculated by solving a linear moment fitting problem in each parametric direction. The proposed shell element formulations are shown through numerical studies to be rank sufficient and to be free of spurious modes.  The studies reveal comparable accuracy, in terms of both displacement and stress, compared with fully integrated spline-based shell elements, while at the same time reducing storage and computational cost associated with forming element stiffness and mass matrices and force vectors. The high accuracy with low computational cost makes the proposed quadratures along with higher-order spline bases, in particular polynomial orders, $p=5$ and 6, good choices for alleviating membrane and shear locking in shells.



