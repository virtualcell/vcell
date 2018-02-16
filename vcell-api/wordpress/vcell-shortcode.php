<?php
/*
* Plugin Name: VCell ShortCode
* Description: Shortcode for VCell model content.
* Version: 1.1
* Author: Jim Schaff
*/
register_activation_hook(__FILE__, 'vcell_my_activation');

function vcell_my_activation() {
	vcell_log_me('my_activation() invoked ... running do_this_hourly() immeciately upon activation ... and then schedule it periodically');
	vcell_do_this_hourly();
#	if (! wp_next_scheduled('vcell_hourly_event' )) {
#		wp_schedule_event(time(), 'hourly', 'vcell_hourly_event');
#	}
}

#add_action('vcell_hourly_event', 'vcell_do_this_hourly');

function vcell_do_this_hourly(){
	vcell_log_me('hourly processing begun');
	$publications =& vcell_get_publications_object();
	vcell_log_me('inserting publications and biomodel posts as needed');
	foreach($publications as $pub){
		# make sure all publication posts are installed.
		$pubPost =& vcell_find_publication_post($pub->pubKey);
		if (is_null($pubPost)){
			vcell_insert_publication_post($pub);
			$pubPost =& vcell_find_publication_post($pub->pubKey);
		}
		if (!empty($pub->biomodelReferences)){
			foreach ($pub->biomodelReferences as $bioref){
				$bmKey = $bioref->bmKey;
//				$biomodelvcml =& vcell_get_biomodel_vcml_object($bmKey);
				$biomodel =& vcell_get_biomodel_object($bmKey);
				$bmPost =& vcell_find_biomodel_post($bmKey);
				if (is_null($bmPost)){
					vcell_insert_biomodel_post($biomodel);
				}else{
					$biomodelPostId = $bmPost->ID;
					vcell_update_biomodel_post($biomodel,$biomodelPostId);
				}
			}
		}
		vcell_log_me('updating publications post ' . $pubPost->ID);
		vcell_update_publication_post($pub, $pubPost->ID);
	}
	vcell_log_me('hourly processing complete');
}

function &vcell_find_biomodel_post($bmKey){
	$args = array(
			'meta_key' => 'bmKey',
			'meta_value' => $bmKey,
			'category_name' => 'biomodel',
			'post_type' => 'post',
			'post_status' => 'any',
			'posts_per_page' => -1
	);
	$posts = get_posts($args);
	if (is_null($posts) || empty($posts)){
		$posts = NULL;
		return $posts;
	}else if (is_array($posts)){
		return $posts[0];
	}else{
		return $posts;
	}
}

function vcell_insert_biomodel_post($biomodel){
	$biomodelCatID = get_cat_ID('biomodel');
	if ($biomodelCatID == 0){
		$biomodelCatID = wp_create_category('biomodel');
	}
	$post_arr = array(
		'post_title'   => 'biomodel-' . $biomodel->bmKey,
		'post_content' => vcell_get_biomodel_post_content($biomodel),
		'post_status'  => 'publish',
		'post_author'  => get_current_user_id(),
		'meta_input'   => array(
			'bmKey' => $biomodel->bmKey,
		),
		'post_category' => array( $biomodelCatID )
	);
	$post_id = wp_insert_post($post_arr);
	return $post_id;
}

function vcell_update_biomodel_post($biomodel, $biomodelPostId){
	$biomodelCatID = get_cat_ID('biomodel');
	if ($biomodelCatID == 0){
		$biomodelCatID = wp_create_category('biomodel');
	}
	$post_arr = array(
		'ID'		   => $biomodelPostId,
		'post_title'   => 'biomodel-' . $biomodel->bmKey,
		'post_content' => vcell_get_biomodel_post_content($biomodel),
		'post_status'  => 'publish',
		'post_author'  => get_current_user_id(),
		'meta_input'   => array(
			'bmKey' => $biomodel->bmKey,
		),
		'post_category' => array( $biomodelCatID )
	);
	$post_id = wp_update_post($post_arr);
	return $post_id;
}

function &vcell_find_publication_post($pubKey){
	$args = array(
			'meta_key' => 'pubKey',
			'meta_value' => $pubKey,
			'category_name' => 'publication',
			'post_type' => 'post',
			'post_status' => 'any',
			'posts_per_page' => -1
	);
	$posts = get_posts($args);
	if (is_null($posts) || empty($posts)){
		$posts = NULL;
		return $posts;
	}else if (is_array($posts)){
		return $posts[0];
	}else{
		return $posts;
	}
}

function vcell_update_publication_post($publication, $pubPostId){
	$publicationCatID = get_cat_ID('publication');
	if ($publicationCatID == 0){
		$publicationCatID = wp_create_category('publication');
	}
	$publicationYearCatName = 'publication-' . $publication->year;
	$publicationYearCatID = get_cat_ID($publicationYearCatName);
	if ($publicationYearCatID == 0){
		$publicationYearCatID = wp_create_category($publicationYearCatName);
	}
	$post_arr = array(
		'ID'		   => $pubPostId,
		'post_title'   => vcell_get_publication_post_title($publication),
		'post_content' => vcell_get_publication_post_content($publication),
		'post_status'  => 'publish',
		'post_author'  => get_current_user_id(),
		'meta_input'   => array(
			'pubKey' => $publication->pubKey,
		),
		'post_category' => array( $publicationCatID, $publicationYearCatID )
	);
	$post_id = wp_update_post($post_arr);
	return $post_id;
}

function vcell_insert_publication_post($publication){
	$publicationCatID = get_cat_ID('publication');
	if ($publicationCatID == 0){
		$publicationCatID = wp_create_category('publication');
	}
	$publicationYearCatName = 'publication-' . $publication->year;
	$publicationYearCatID = get_cat_ID($publicationYearCatName);
	if ($publicationYearCatID == 0){
		$publicationYearCatID = wp_create_category($publicationYearCatName);
	}
	$post_arr = array(
		'post_title'   => vcell_get_publication_post_title($publication),
		'post_content' => vcell_get_publication_post_content($publication),
		'post_status'  => 'publish',
		'post_date'    => date("Y-m-d H:i:s", strtotime("Jan 1, " . $publication->year) ),
		'post_author'  => get_current_user_id(),
		'meta_input'   => array(
			'pubKey' => $publication->pubKey,
		),
		'post_category' => array( $publicationCatID, $publicationYearCatID )
	);
	$post_id = wp_insert_post($post_arr);
	return $post_id;
}

function &vcell_get_biomodel_object($bmKey) {
	$json = file_get_contents("https://vcellapi.cam.uchc.edu:8080/biomodel/" . $bmKey);
	$biomodel = json_decode($json);
	return $biomodel;
}

function &vcell_get_biomodel_vcml_object($bmKey){
	$vcmlString = file_get_contents("https://vcellapi.cam.uchc.edu:8080/biomodel/" . $bmKey . '/biomodel.vcml');
    vcell_log_me("bmKey is " . $bmKey . ", vcmlString is '" . $vcmlString . "'");
    $biomodel_vcml = new SimpleXMLElement($vcmlString) or die('unable to retrieve vcml');
	return $biomodel_vcml;
}

function &vcell_get_publication_object($pubKey) {
	$json = file_get_contents("https://vcellapi.cam.uchc.edu:8080/publication/" . $pubKey);
	$publication = json_decode($json);
	return $publication;
}

function &vcell_get_publications_object() {
vcell_log_me('calling vcellapi for publications');
	$json = file_get_contents("https://vcellapi.cam.uchc.edu:8080/publication");
vcell_log_me('vcellapi publications json = ' . $json );
	$publications = json_decode($json);
	return $publications;
}



/**
*  Post title
*/
function vcell_get_publication_post_title($publication) {
	$authorText = '';
	if (sizeof($publication->authors)>1){
		$authorText = explode(',', reset($publication->authors))[0] . ' et al., ';
	}else{
		$authorText = reset($publication->authors) . ', ';
	}
	$authorText .= $publication->year;
	return $authorText;
}

/**
*  Post content
*/
function vcell_get_publication_post_content($publication) {
	$authorText = '';
	$bFirst = 1;
	foreach ($publication->authors as $author){
		if ($bFirst == 1){
			$authorText = $author;
			$bFirst = 0;
		}else{
			$authorText .= ", " . $author;
		}
	}
	$modellinktext = '';

	vcell_log_me('getting publication post content for pubKey ' . $publication->pubKey);
	if (sizeof($publication->biomodelReferences)>0){
		$modellinktext .= '&nbsp;&nbsp;&nbsp;VCell BioModels referenced in publication:<br/>';
		foreach ($publication->biomodelReferences as $biomodelref){
			$bmKey = $biomodelref->bmKey;
			$bmName = $biomodelref->name;
			$ownerKey = $biomodelref->ownerKey;
			$ownerName = $biomodelref->ownerName;
			$bmPost =& vcell_find_biomodel_post($bmKey);
			if (is_null($bmPost)){
				$bmurl = '/biomodel-' . $bmKey;  # this is a placeholder
				vcell_log_me('    using fake link for publication ' . $publication->pubKey . ' biomodel ' . $bmKey);
			}else{
				$bmurl = get_permalink($bmPost->ID);
				vcell_log_me('    using real link for publication ' . $publication->pubKey . ' biomodel ' . $bmKey);
			}
			$modellinktext .= '&nbsp;&nbsp;&nbsp;user: ' . $ownerName . ', &nbsp;&nbsp;model name: <a href="' . $bmurl . '">' . $bmName . '</a><br/>';
		}
	} 

	if (sizeof($publication->mathmodelReferences)>0){
		$modellinktext .= '&nbsp;&nbsp;&nbsp;VCell MathModels referenced in publication:<br/>';
		foreach ($publication->mathmodelReferences as $mathmodelref){
			$mmKey = $mathmodelref->mmKey;
			$mmName = $mathmodelref->name;
			$ownerKey = $mathmodelref->ownerKey;
			$ownerName = $mathmodelref->ownerName;
			$mmurl = '/mathmodel-' . $mmKey;   # this won't work
			$modellinktext .= '&nbsp;&nbsp;&nbsp;user: ' . $ownerName . ', &nbsp;&nbsp;model name: <a href="' . $mmurl . '">' . $mmName . '</a><br/>';
		}
	} 

	$text = '<strong><big>' . $publication->title . '</big></strong><br/>';
	$text .= $authorText . '<br/>';
	$text .= $publication->citation . '<br/>';
	$publinktext = '';
	if (!empty($publication->pubmedid)){
		$pubmedurl = 'http://www.ncbi.nlm.nih.gov/pubmed/' . $publication->pubmedid;
		$pubmedlabel = 'PUBMED:' . $publication->pubmedid;
		$publinktext .= '<a href="' . $pubmedurl . '">' . $pubmedlabel . '</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
	}
	if (!empty($publication->doi)){
		$doiurl = 'https://dx.doi.org/' . $publication->doi;
		$doilabel = 'doi:' . $publication->doi;
		$publinktext .= '<a href="' . $doiurl . '">' . $doilabel . '</a>';
	}
	$text .= $publinktext . '<br/>';
	$text .= $modellinktext;
	return $text;
}

function vcell_date_from_timestamp($vcell_timestamp) {
	$myDate = $vcell_timestamp/1000;
	$datetext = gmdate("D, d M Y", $myDate) . '&nbsp;&nbsp;' . gmdate("G:i:s \G\M\T", $myDate);
	return $datetext;
}

function vcell_get_biomodel_post_content($biomodel){
	$bmKey = $biomodel->bmKey;
	vcell_log_me('getting biomodel post content for bmKey ' . $bmKey . ' with name "' . $biomodel->name . '"');
	
	$compartmenttext = '[vcell-biomodel-compartments bmkey="' . $bmKey . '"]';
	$nametext = 'Name: <strong>"' . $biomodel->name . '"</strong>';
	$datetext = 'Saved: <strong>' . vcell_date_from_timestamp($biomodel->savedDate) . '</strong>';
	$ownertext = 'Owned by: <strong>"' . $biomodel->ownerName . '"</strong>';
	$bmBaseUrl = 'https://vcellapi.cam.uchc.edu:8080/biomodel/' . $bmKey;
	$bmIdentifier = 'vcell identifier: <strong>biomodel-' . $bmKey . '</strong>';
	$filename = 'VCBioModel_' . $bmKey . '.vcml';
	$downloadtext = '<a href="' . $bmBaseUrl . '/biomodel.vcml" type="application/vcml+xml" download="' . $filename . '">download model (.vcml)</a>';
	$diagramtext = '<img src="https://vcellapi.cam.uchc.edu:8080/biomodel/' . $bmKey . '/diagram"/>';
	return $nametext . '<br/>' . $datetext . '<br/>' . $bmIdentifier . '<br>' . $ownertext . '<br/><br/>' . $downloadtext . '<br/><br/>' . $diagramtext . '<br/><br/>' . $compartmenttext;
}

function vcell_get_biomodel_compartment_content($biomodelvcml){
	#
	# parse xml for compartments, species, reactions into nice arrays (avoiding nested iterators of SimpleXMLElement)
	#
	$compArray = [];
	$reactionArray = [];
	$speciesArray = [];
	$modelChildren = $biomodelvcml->BioModel->Model->children();
	foreach($modelChildren as $modelChild){
		if ($modelChild->getName() == "Feature" OR $modelChild->getName() == "Membrane"){
			$structureElement = $modelChild;
			$compartmentType = '';
			if ($structureElement->getName() == "Feature"){
				$compartmentType = "Volume";
			}else if ($structureElement->getName() == "Membrane"){
				$compartmentType = "Membrane";
			}
			array_push($compArray, array(
				'type'		=> $compartmentType,
				'name'		=> $structureElement['Name']
				));
		}else if ($modelChild->getName() == "LocalizedCompound"){
			$speciesElement = $modelChild;
			array_push($speciesArray, array(
				'structure'		=> $speciesElement['Structure'],
				'name'			=> $speciesElement['Name']
				));
		}else if ($modelChild->getName() == "SimpleReaction" OR $modelChild->getName() == "FluxStep"){
			$reactionElement = $modelChild;
			$reactants = [];
			$products = [];
			foreach($reactionElement->children() as $reactionChild){
				if ($reactionChild->getName() == "Reactant"){
					$reactantElement = $reactionChild;
					array_push($reactants, array(
						'species'					=> $reactantElement['LocalizedCompoundRef'],
						'stoichiometry'				=> $reactantElement['Stoichiometry']
						));
				}
				if ($reactionChild->getName() == "Product"){
					$productElement = $reactionChild;
					array_push($products, array(
						'species'					=> $productElement['LocalizedCompoundRef'],
						'stoichiometry'				=> $productElement['Stoichiometry']
						));
				}
			}
			array_push($reactionArray, array(
				'structure'		=> $reactionElement['Structure'],
				'name'			=> $reactionElement['Name'],
				'reactants'		=> $reactants,
				'products'		=> $products
				));
		}
	}
	$compartmenttext = '<strong>Compartments, Species, Reactions</strong><br/><ul>';
	foreach($compArray as $compartment){
		$compartmenttext .= '<li>';
		$compartmenttext .= ' Compartment <strong>"' . $compartment['name'] . '"</strong>. ' . ' &nbsp; (type: ' . $compartment['type'] . ')';
		#
		# add text for species in this compartment
		#
		$speciestext = '';
		foreach($speciesArray as $species){
			if (strcmp($species['structure'],$compartment['name'])==0) {
				$speciestext .= '<li>Species <strong>"' . $species['name'] . '"</strong></li>';
			}
		}

		#
		# add text for reactions in this compartment
		#
		$reactiontext = '';
		$null_set_symbol = '&#x2205;';  // null character
		$arrow_symbol = '&#8594;'; // arrow character
		#$null_set_symbol = '&nbsp;&nbsp;&nbsp;&nbsp;';  // null character
		#$arrow_symbol = '--&gt;'; // arrow character
		foreach($reactionArray as $reaction){
			if (strcmp($reaction['structure'],$compartment['name'])==0) {
				$reactiontext .= '<li>';
				$reactiontext .= 'Reaction <strong>"' . $reaction['name'] . '"</strong> &nbsp; ';
				$reactiontext .= '(';
				$reactants = [];
				if (count($reaction['reactants'])>0){
					foreach ($reaction['reactants'] as $reactant){
						if ($reactant['stoichiometry'] == "1"){
							$reactiontext .= $reactant['species'];
						}else{
							$reactiontext .= $reactant['stoichiometry'] . '&nbsp;' . $reactant['species'];
						}
						if ($reactant != end($reaction['reactants'])){
							$reactiontext .= ' + ';
						}
					}
				}else{
					$reactiontext .= $null_set_symbol;
				}
				
				$reactiontext .= ' ' . $arrow_symbol . ' ';
				if (count($reaction['products'])>0){
					foreach ($reaction['products'] as $product){
						if ($product['stoichiometry'] == 1){
							$reactiontext .= $product['species'];
						}else{
							$reactiontext .= $product['stoichiometry'] . '&nbsp;' . $product['species'];
						}
						if ($product != end($reaction['products'])){
							$reactiontext .= ' + ';
						}
					}
				}else{
					$reactiontext .= $null_set_symbol;
				}
				$reactiontext .= ')</li>';
			}
		}
		if (strlen($reactiontext)>0 OR strlen($speciestext)>0){
			$compartmenttext .= '<ul>' . $speciestext . $reactiontext . '</ul>';
		}
		$compartmenttext .= '</li>';
	}
	$compartmenttext .= '</ul>';
	return $compartmenttext;
}

function vcell_log_me($message) {
    if (WP_DEBUG === true) {
        if (is_array($message) || is_object($message)) {
            error_log(print_r($message, true));
        } else {
            error_log($message);
        }
    }
}

function vcell_get_model_detail($atts) {
vcell_log_me('calling shortcode vcell-biomodel-compartments with bmKey = ' . $atts['bmkey'] );
vcell_log_me($atts);
	$bmKey = $atts['bmkey'];
vcell_log_me('in shortcode vcell-biomodel-compartments using bmKey = ' . $bmKey );
	try {
        $biomodelvcml =& vcell_get_biomodel_vcml_object($bmKey);
        $text = vcell_get_biomodel_compartment_content($biomodelvcml);
    } catch (Exception $e) {
		echo 'Caught exception: ', $e->getMessage(), "\n";
		$text = "model details unavailable for this model";
	}
vcell_log_me('returning ' . $text);
	return $text;
}

add_shortcode('vcell-biomodel-compartments','vcell_get_model_detail');

/**
 *
 * testing post content in fiddle
 *
 */
/*
function test_biomodel(){
	$bmKey = 2917738;
	$biomodel =& vcell_get_biomodel_object($bmKey);
	$biomodelvcml =& vcell_get_biomodel_vcml_object($bmKey);
	return vcell_get_biomodel_post_content($biomodel, $biomodelvcml);
}

function test_model_publication(){
	$pubKey = 102336415;
	$publication =& vcell_get_publication_object($pubKey);
	return vcell_get_publication_post_title($publication) . '<br/>' . vcell_get_publication_post_content($publication);
}

#  fake functions for use in phpFiddle
function vcell_log_me($message) {
    print($message);
}

function register_activation_hook(){}
function add_shortcode(){}
function add_action(){}

print("<h1>Publication Example</h1>");
print(test_model_publication());
print("<h1>Biomodel Example</h1>");
print(test_biomodel());
*/
?>


