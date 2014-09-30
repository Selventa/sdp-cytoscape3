Changelog
=========



Version 1.3.7
=============

    - Fixes:
        - Model save error when invalid data exists in the evidence column (`bug #623`_)

Version 1.3.6
=============

    - Fixes (regression):
        - Search by species in Import Model (`bug #608`_)

Version 1.3.5
=============

    - Fixes:
        - Load additional search results as needed in *Import Model*, *Add Comparison*, and *Add Rcr Result* dialogs (`bug #580`_)
    - Enhancements:
        - UI improvements to *Import Model*, *Add Comparison*, and *Add Rcr Result* dialogs:
           - Better layout
           - Added column control to show/hide columns
           - Added ``Description`` column
           - Wrap text columns as needed
           - Find text in search results (Key: ``Control + F``)

Version 1.3.4
=============

    - Fixes:
        - Error when saving metadata when model has empty nodes or edges (`bug #569`_)
        - Table columns not defined on subsequent save of new model (`bug #570`_)

Version 1.3.3
=============

    - Fixes:
        - Fix incorrect association of metadata on import of model (`bug #541`_)

Version 1.3.2
=============

    - Fixes:
        - Forbid overwriting model on save if revisions conflict (`bug #563`_)
        - Provide useful error messages when configuring SDP access in cytoscape (`enhancement #564`_)

Version 1.3.1
=============

    - Fixes:
        - Choose correct type for model metadata column based on data values.

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
.. _bug #541: https://redmine.selventa.com/issues/541
.. _bug #563: https://redmine.selventa.com/issues/563
.. _bug #569: https://redmine.selventa.com/issues/569
.. _bug #570: https://redmine.selventa.com/issues/570
.. _enhancement #564: https://redmine.selventa.com/issues/564
.. _bug #580: https://redmine.selventa.com/issues/580
.. _bug #608: https://redmine.selventa.com/issues/608
.. _bug #623: https://redmine.selventa.com/issues/623
