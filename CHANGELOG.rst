Changelog
=========

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
