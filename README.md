Minimal tester web app serving Clay Kinds as HTML with Ring.

![demo](https://github.com/user-attachments/assets/36845bde-a05f-4a23-a0b5-5b6616fcb541)

# Usage

## Editor

1. Jack in REPL
2. Evaluate `server.clj` buffer
3. Access http://localhost:3000
4. Quit REPL and restart again after making code changes

# Server notes

Server uses Ring, Jetty, Ruuter and HTMX JavaScript front-end library loading HTML dynamically to view.
Kit framework was used partially as example also.
See notes in `server.clj`.

Code is not reloaded after changes as in https://ericnormand.me/guide/clojure-web-tutorial and require restarting REPL because Ruuter routing library (https://github.com/askonomm/ruuter) is apparently not correctly set to load in dynamic fashion.

View code is duplicated just to keep it simple.

# Clay example notes
Examples from Clay docs are used with some changes for randomizing data between requests https://scicloj.github.io/clay/clay_book.examples.html.

## Problems with more complex JS deps

Some examples had more trouble rendering, eg. because of more complicated dependencies. Some failing examples:

- Vega
- Leaflet
- 3DMol.js (not included)
- D3 (not included)
- Portal
- Datatable (does not load dependency for styling)

See JS console for hints..

## inline-js-and-css is not working

There is an issue with the Clay option which should include Kind dependencies as inline script at the moment https://github.com/scicloj/clay/issues/144.

# WordPress Plugin Demo
Demonstrates embedding the content on WordPress Admin panel. To install, place `wp-plugin-example` in `path-to-www-root/wp-content/plugins/` and activate it in plugins panel.

![Screenshot_20240810_124013](https://github.com/user-attachments/assets/40a7694a-febf-4472-bde6-1277a2bfc0ca)

