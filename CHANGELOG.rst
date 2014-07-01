Changelog
=========

- `Version 1.3.0`_
    - Enhancements:
        - Improved Search Nodes
        - Improved usability of Evidence panel
        - Provide BEL statement as edge name
    - Fixes:
        - Fixed setting of "bel.function" column

- `Version 1.2.0`_
    - New Feature:
        - Validation of BEL nodes and edges

- `Version 1.1.0`_
    - New Feature:
        - KamNav Configuration
    - Enhancements:
        - Knowledge Neighborhood
        - Performance of loading supporting evidence
    - Fixes:
        - `bug #20`_

- `Version 1.0.0`_
    - Enhancements:
        - New deployment technique.

Version 1.3.0
=============

    - Enhancements:
        - Improved Search Nodes
            - Supports wildcard ``*`` anywhere in entity (e.g. ``*kt``, ``mapk*``, ``*aging*``)
            - Case-insensitive search on entity
            - Search across *All* namespaces (**now the default**)
        - Improved usability of Evidence panel
            - The annotation table wraps text and allows easier selection for copy/paste
        - Provide BEL statement as edge name
            - Edge names are set as a BEL statement
            - Loading models will compute edge name from [source, rel, target]
            - Retrieve edges from knowledge network will set edge name
    - Fixes:
        - Fixed "bel.function" to be set correctly when building model nodes from scratch

Version 1.2.0
=============

    - New Feature:
        - Validation of BEL nodes and edges of a network
            - Summary dialog provides percentages of valid and invalid BEL
            - Added columns 'valid bel' and 'validation error' to node and edge tables
            - Visualize invalid nodes and edges as red

Version 1.1.0
=============

    - New Feature:
        - Configuration of multiple OpenBEL servers (KamNav -> Configure).
    - Enhancements:
        - Improve load time of supporting evidence.  This will improve Knowledge Neighborhood, Link to Knowledge Network, and Expand Node.
        - Update facets for Knowledge Neighborhood.
            - Added Direction (upstream/downstream), Function, and Relationship
            - Removed Statement and Edge facets
            - Cleaner table view for evidence
    - Fixes:
        - Create new network if necessary when adding after Search Nodes. (`bug #20`_)

Version 1.0.0
=============

- Enhancements
    - Supplement Java Webstart deployment with getdown deployment.  This provides a more reliable update procedure and distributes the correct Java runtime per client.

.. _bug #20: https://github.com/OpenBEL/kam-nav/issues/20
