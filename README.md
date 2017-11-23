# A wrapper to integrate Apache ODE solvers in the KNIME analytics platform

This KNIME node solves first order Ordinary Differential Equations (ODE) using the algorithms provided in the Apache Commons Math library.

Ordinary Differential Equations (ODE) are mathematical tools used to model complex systems and to simulate their evolution. 
This node provides integrators to solve Initial Value Problems of the form y'=f(t,y) with t0 and y(t0)=y0 known. 
The integrators compute an estimate of y(t) from t=t0 to t=t1.

  <p>
  Example of the Lotka-Volterra equations (predator-prey):
      <ul>
        <li> dy0/dt = alpha * y0 - beta * y0 * y1 </li>
        <li> dy1/dt = delta * y0 * y1 - gamma * y1</li>
      </ul>
      where
      <ul>
        <li> y0 is the number of preys; </li>
        <li> y1 is the number of predators; </li>
        <li> dy0/dt and dy1/dt represent the growth rate of the two populations over time;</li>
        <li> t represents time;</li> 
        <li> alpha, beta, delat and gamma  are positive real parameters describing the interaction of the two species.</li>
      </ul>
  </p>      

        
This node is based on the ODE package developed by Apache Commons (Apache Commons Math TM, see http://commons.apache.org/proper/commons-math/userguide/ode.html) and it provides an integration of these algorithms in the KNIME platform.
Two types of integrators are available: Fixed step integrators and Adaptive step integrators.

Fixed step integrators are :
        			<ol>
        				<li>Euler -- order: 1</li>
        				<li>Midpoint -- order: 2</li>
        				<li>Classical Runge-Kutta -- order: 4</li>
        				<li>Gill -- order: 4</li>
        				<li>3/8 -- order: 4</li>
        				<li>Luther -- order: 6</li>
        			</ol>
              
Adaptive stepsize integrators are:
        			<ol>
        				<li>Higham and Hall 	-- integration order: 5 -- error estimation order: 4 </li>
        				<li>Dormand-Prince 5(4) -- integration order: 5 -- error estimation order: 4</li>
        				<li>Dormand-Prince  8(5,3) -- integration order: 8 -- error estimation order: 5 and 3</li>
        				<li>Gragg-Bulirsch-Stoer -- integration order: variable (up to 18 by default) -- error estimation order: variable</li>
        				<li>Adams-Bashforth -- integration order: variable -- error estimation order: variable</li>
        				<li>Adams-Moulton -- integration order: variable -- error estimation order: variable</li>
        			</ol>        	    

## Installation in KNIME

* Import the project in the Eclipse KNIME SDK (https://www.knime.com/downloads)
* Generate a jar using the "Export deployable plug-ins and fragments" wizard
* Copy the jar to the "dropins" folder of the KNIME / Eclipse installation


## Contributors

* Frédéric Flouvat, Associate professor in comptuer science, Institue of Exact and Applied Sciences, University of New Caledonia, New Caledonia.
* Cyril Metayer, Associate professor in physics, Institue of Exact and Applied Sciences, University of New Caledonia, New Caledonia.


## Feedbacks
If you have any questions or suggestions concerning use and development of this KNIME node, please mail to Frédéric Flouvat.
