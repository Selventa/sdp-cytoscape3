sdp-cytoscape3
==============

A distribution of cytoscape3 apps for model building with the SDP.  The cytoscape distribution is opened via Java Web Start using the launcher_ code.  The launcher starts cytoscape separately from the Java Web Start process.

It includes the following apps:

- `OpenBEL Nav`_: Explore knowledge networks
- `Model Builder`_: Build biological networks with supporting data from knowledge networks.

project structure
-----------------

This project provides tools and scripts necessary to build and test within a self-contained cytoscape installation.

- Cytoscape apps are contained within the apps_ directory.
- The cytoscape launch code run via Java Web Start is contained within launcher_ directory.
- Build resources are contained within the resources_ directory.
- The build environment's scripts are contained within the scripts_ directory.
- The build environment tools are contained within the tools_ directory.

The build environment config is defined in env.sh_.  It provides a bash repl that allows the developer to run different steps of the build.  This is defined in go.sh_.

.. _OpenBEL Nav: https://github.com/OpenBEL/kam-nav
.. _Model Builder: https://github.com/Selventa/model-builder
.. _apps: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/apps
.. _launcher: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/launcher
.. _resources: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/resources
.. _scripts: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts
.. _tools: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/tools
.. _env.sh: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/env.sh
.. _go.sh: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/go.sh
