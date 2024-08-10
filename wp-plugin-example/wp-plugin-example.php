<?php

/**
 * Plugin Name: Example WP plugin
 * Description: Embeds Clojure server content from localhost:3000/
 * Author: Jarkko Saltiola
 */

/* Iframe rendering in the dashboard widget */
function clj_example_output_iframe() {
	echo '<iframe src="http://localhost:3000" width="100%" height="700" scrolling="no"></iframe>';
}

//Dashboard initialisation hook
add_action('wp_dashboard_setup', function(){
	wp_add_dashboard_widget('embed-clj-widget', 'localhost:3000 (iframe)', 'clj_example_output_iframe');
});

// TODO Add setup X-Frame-Options settings on serverside if needed
// TODO handle authentication with token set in env or WP settings & check on server
