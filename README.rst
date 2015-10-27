sdp-cytoscape3
==============

A distribution of cytoscape3 apps for model building with the SDP.  The cytoscape distribution is opened via Java Web Start using the launcher_ code.  The launcher starts cytoscape separately from the Java Web Start process.

It includes the following apps:

- `OpenBEL Navigator`_: Explore knowledge networks
- `Model Builder`_: Build biological networks with supporting data from knowledge networks.

project structure
-----------------

This project provides tools and scripts necessary to build and test within a self-contained cytoscape installation.

- Cytoscape apps are contained within the apps_ directory.  Each app uses a similar project structure that allows rapid development, integration and deployment.
- The cytoscape launch code run via Java Web Start is contained within launcher_ directory.
- Build resources are contained within the resources_ directory.
- The build environment's scripts are contained within the scripts_ directory.
- The build environment tools are contained within the tools_ directory.

The build environment config is defined in env.sh_.  It provides a bash repl that allows the developer to run different steps of the build.  This is defined in go.sh_.

development commands
--------------------

Use the go.sh_ script to run build commands during development.  Some important options are:

- build_

  - builds the apps/distribution

- test_

  - builds and test the apps/distribution

- deploy_

  - deploy apps to tools/cytoscape

- undeploy_

  - undeploy apps from tools/cytoscape

- loop-deploy_

  - build and deploy apps to tools/cytoscape

- package_

  - package the sdp-cytoscape distribution

- start_

  - start cytoscape in tools/cytoscape

- stop_

  - stop cytoscape in tools/cytoscape

environment customisation
-------------------------

Scripts and tools are driven by environment variables in env.sh_.  Environment variables can be overriden in an ``env.sh.custom`` file that you must create (this file is ignored by git).

You might want to do the following:

- build a different set of apps

  - set the ``DEV_APPS_DIR`` environment variable to your directory contains apps

- use a different cytoscape installation to deploy apps into

  - set the ``DEV_CY3_DIR`` environment variable to your cytoscape directory

- set the log file for tools/cytoscape

  - set the ``DEV_CY3_LOG_FILE`` environment variable to the log file path

.. _OpenBEL Navigator: https://github.com/OpenBEL/kam-nav
.. _Model Builder: https://github.com/Selventa/model-builder
.. _apps: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/apps
.. _launcher: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/launcher
.. _resources: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/resources
.. _scripts: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts
.. _tools: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/tools
.. _env.sh: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/env.sh
.. _go.sh: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/go.sh
.. _build: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/build.sh
.. _test: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/test.sh
.. _deploy: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/deploy.sh
.. _undeploy: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/undeploy.sh
.. _loop-deploy: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/loop-deploy.sh
.. _package: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/package.sh
.. _start: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/start.sh
.. _stop: https://github.com/Selventa/sdp-cytoscape3/tree/experimental/scripts/stop.sh

tickle me elmo




