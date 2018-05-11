/*
 * @(#)LiteMorph_en.java	1.6 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @(#) LiteMorph_en.java 1.6 - last change made 10/30/06
 */

package com.sun.java.help.search;


import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This is the English version of LiteMorph
 *
 * @see LiteMorph
 */
public class LiteMorph_en extends LiteMorph{


    private static LiteMorph morph = new LiteMorph_en();

    /**
     * Make this a singleton class
     */
    private LiteMorph_en() {
    }

    /**
     * Return the LiteMorph for this class
     */
    public synchronized static LiteMorph getMorph() {
	return morph;
    }

    /**
     * This is a locale specific intialization.
     * Create the exceptions HashTable for the size needed.
     * the call intialize(String []).
     */
    protected synchronized void initialize() {
	if (exceptions != null) {
	    return;
	}
	exceptions = new Hashtable(2800, (float)0.7);

	// The exceptions: 
	//	current statistics: approx 700 variation groups
	//		approx 2800 words)
	String[] exceptionTable = {
	    "a",
	    //opt//"aardwolf aardwolves",
	    "abandoner abandon abandons abandoned abandoning abandonings abandoners",
	    "abdomen abdomens",
	    "about",
	    "above",
	    //opt//"abscissa abscissas abscissae",
	    "acid acids acidic acidity acidities",
	    "across",
	    "act acts acted acting actor actors",
	    //opt//"acumen",
	    "ad ads", 
	    "add adds added adding addings addition additions adder adders",
	    //opt//"addendum addenda addendums",
	    //opt//"adieu adieux adieus",
	    "advertise advertises advertised advertising advertiser advertisers "
	    +"advertisement advertisements advertisings",
	    "after",
	    "again",
	    "against",
	    "ago",
	    //opt//"agoutis agouti",
	    //opt//"alias aliases",
	    "all",
	    "almost",
	    "along",
	    "already",
	    "also",
	    "although",
	    "alumna alumnae alumnus alumni",
	    //opt//"alveolus alveoli alveolar",
	    "always",
	    "amen amens",
	    "amidships",
	    "amid amidst",
	    "among amongst",
	    //opt//"ampulla ampullae",
	    //opt//"amygdala amygdalae",
	    "an",
	    "analysis analyses",
	    "and",
	    //opt//"annulus annuluses annuli annular",
	    "another other others",
	    "antenna antennas antennae",
	    "antitheses antithesis",
	    "any",
	    "anyone anybody",
	    "anything",
	    "appendix appendixes appendices",
	    "apropos",
	    "aquarium aquariums aquaria",
	    "argument arguments argue argues argued arguing arguings arguer arguers",
	    "arise arises arose arisen ariser arisers arising arisings",
	    "around",
	    "as",
	    "asbestos",
	    "at",
	    "atlas atlases",
	    //opt//"atrium atriums atria",
	    "auger augers augered augering augerings augerer augerers",
	    "augment augments augmented augmenting augmentings augmentation "
	    +"augmentations augmenter augmenters",
	    "automata automaton automatons",
	    "automation automating automate automates automated automatic",
	    "avoirdupois",
	    "awake awakes awoke awaked awoken awaker awakers awaking awakings "
	    +"awakening awakenings",
	    "away",
	    "awful awfully awfulness",
	    "axis axes axises",
	    "bacillus bacilli",
	    "bacterium bacteria",
	    "bad worse worst badly badness",
	    //opt//"balladier balladiers",
	    //opt//"bandolier bandoliers",
	    "bas",
	    "bases basis",
	    "bases base based basing basings basely baseness basenesses basement "
	    +"basements baseless basic basics",
	    //opt//"bateau bateaux",
	    //opt//"bathos",
	    //opt//"bayou bayous",
	    "be am are is was were been being",
	    "bear bears bore borne bearing bearings bearer bearers",
	    "beat beats beaten beating beatings beater beaters",
	    //opt//"beau beaux beaus",
	    "because",
	    "become becomes became becoming",
	    //opt//"bedesman bedesmans",
	    "beef beefs beeves beefed beefing",
	    "beer beers",
	    "before",
	    //opt//"beget begets begat begot begotten begetting begettings begetter begetters",
	    "begin begins began begun beginning beginnings beginner beginners",
	    "behalf behalves",
	    "being beings",
	    "bend bends bent bending bendings bender benders",
	    "bereave bereaves bereaved bereft bereaving bereavings bereavement "
	    +"bereavements",
	    "beside besides",
	    "best bests bested besting",
	    "bet bets betting bettor bettors",
	    "betimes",
	    "between",
	    "beyond",
	    "bid bids bade bidden bidding biddings bidder bidders",
	    "bier biers",
	    "bind binds bound binding bindings binder binders",
	    "bit bits",
	    "bite bites bit bitten biting bitings biter biters",
	    "blackfoot blackfeet",
	    "bleed bleeds bled bleeding bleedings bleeder bleeders",
	    "blow blows blew blown blowing blowings blower blowers",
	    "bookshelf bookshelves",
	    //opt//"borzois borzoi",
	    "both",
	    "bound bounds bounded bounding boundings bounder bounders boundless", 
	    "bourgeois bourgeoisie",
	    "bra bras",
	    "brahman brahmans",
	    "break breaks broke broken breaking breakings breaker breakers",
	    "breed breeds bred breeding breedings breeder breeders",
	    "bring brings brought bringing bringings bringer bringers",
	    //opt//"bronchus bronchi bronchial bronchially",
	    "build builds built building buildings builder builders",
	    //opt//"bum bums bummed bumming bummings bummer bummers",
	    //opt//"bursa bursae bursas bursal",
	    "bus buses bused bussed busing bussing busings bussings buser busers "
	    +"busser bussers",
	    "buss busses bussed bussing bussings busser bussers",
	    "but",
	    "buy buys bought buying buyings buyer buyers",
	    "by",
	    //opt//"caiman caimans cayman caymans",
	    //opt//"calculus calculi",
	    "calf calves calved calving calvings calver calvers",
	    "can cans canned canning cannings canner canners",
	    "can could cannot",
	    //opt//"candelabrum candelabra candelabrums",
	    "canoes canoe canoed canoeing canoeings canoer canoers",
	    //opt//"caribou caribous",
	    "catch catches caught catching catchings catcher catchers",
	    //opt//"catharsis catharses",
	    "cement cements cemented cementing cementings cementer cementers",
	    "cent cents",
	    "center centers centered centering centerings centerless",
	    //opt//"chablis",
	    //opt//"chamois",
	    //opt//"chapattis chapatti",
	    //opt//"chateau chateaus chateaux",
	    //opt//"cherub cherubs cherubim",
	    "child children childless childish childishly",
	    "choose chooses chose chosen choosing choosings chooser choosers",
	    //opt//"cicatrix cicatrices cicatricial cicatricose cicatrize cicatrizes "
	    //opt//+"cicatrized cicatrizing cicatrizings cicatrization cicatrixes",
	    //opt//"clement",
	    //opt//"cliches cliche",
	    "cling clings clung clinging clingings clinger clingers",
	    //opt//"codex codices",
	    //opt//"cognomen",
	    "colloquium colloquia colloquiums",
	    //opt//"colossus colossi colossuses",
	    "come comes came coming comings comer comers",
	    "comment comments commented commenting commentings commenter commenters",
	    "compendium compendia compendiums",
	    "complement complements complemented complementing complementings "
	    +"complementer complementers complementary",
	    "compliment compliments complimented complimenting complimentings "
	    +"complimenter complimenters complimentary",
	    "concerto concertos concerti",
	    "condiment condiments",
	    //opt//"continuum continua continuums",
	    "corps",
	    //opt//"corrigendum corrigenda",
	    "cortex cortices cortexes cortical",
	    "couscous",
	    "creep creeps crept creeping creepings creeper creepers creepy",
	    "crisis crises",
	    "criterion criteria criterial",
	    "cryptanalysis cryptanalyses",
	    "curriculum curricula curriculums curricular",
	    //opt//"cyclamen cyclamens",
	    //opt//"czech czechs czechoslovakia czechoslovak czechoslovaks czechoslovakian "
	    //opt//+"czechoslovakians Czech Czechs Czechoslovakia Czechoslovak Czechoslovaks "
	    //opt//+"Czechoslovakian Czechoslovakians",
	    //opt//"dais daises",
	    "datum data",
	    "day days daily",
	    "deal deals dealt dealing dealings dealer dealers",
	    "decrement decrements decremented decrementing decrementings "
	    +"decrementer decrementers decremental",
	    "deer deers",
	    //opt//"deixis deixes",
	    "demented dementia",
	    "desideratum desiderata",
	    //opt//"desman desmans",
	    "diagnosis diagnoses diagnose diagnosed diagnosing diagnostic",
	    "dialysis dialyses",
	    "dice dices diced dicing dicings dicer dicers",
	    "die dice",
	    "die dies died dying dyings",
	    "dig digs dug digging diggings digger diggers",
	    "dive dives diver divers dove dived diving divings",
	    "divest divests divester divesters divested divesting divestings "
	    +"divestment divestments",
	    "do does did done doing doings doer doers",
	    "document documents documented documenting documentings documenter "
	    +"documenters documentation documentations documentary",
	    //opt//"dodecahedron dodecahedra dodecahedrons",
	    "doe does",
	    //opt//"dormouse dormice",
	    "dove doves",
	    "downstairs",
	    "dozen",
	    "draw draws drew drawn drawing drawings drawer drawers",
	    "drink drinks drank drunk drinking drinkings drinker drinkers",
	    "drive drives drove driven driving drivings driver drivers driverless",
	    "due dues duly",
	    "during",
	    "e",
	    "each",
	    "eager eagerer eagerest eagerly eagerness eagernesses",
	    "early earlier earliest",
	    "easement easements",
	    "eat eats ate eaten eating eatings eater eaters",
	    "effluvium effluvia",
	    "either",
	    "element elements elementary",
	    "elf elves elfen",
	    "ellipse ellipses elliptic elliptical elliptically",
	    "ellipsis ellipses elliptic elliptical elliptically",
	    "else",
	    "embolus emboli embolic embolism",
	    "emolument emoluments",
	    "emphasis emphases",
	    "employ employs employed employing employer employers employee "
	    +"employees employment employments employable",
	    "enough",
	    "equilibrium equilibria equilibriums",
	    "erratum errata",
	    "ever",
	    "every",
	    "everything",
	    //opt//"exegeses exegesis",
	    "exotic exotically exoticness exotica",
	    "experiment experiments experimented experimenting experimentings "
	    +"experimenter experimenters experimentation experimental",
	    "extra extras",
	    //opt//"extremum extrema extremums extreme extremely extremeness extremist extremism",
	    "fall falls fell fallen falling fallings faller fallers",
	    "far farther farthest",
	    "fee fees feeless",
	    "feed feeds fed feeding feedings feeder feeders",
	    "feel feels felt feeling feelings feeler feelers",
	    "ferment ferments fermented fermenting fermentings fermentation "
	    +"fermentations fermenter fermenters",
	    "few fewer fewest",
	    //opt//"fez fezzes fezes",
	    "fight fights fought fighting fightings fighter fighters",
	    "figment figments",
	    "filament filaments",
	    "find finds found finding findings finder finders",
	    "firmament firmaments",
	    "flee flees fled fleeing fleeings",
	    "fling flings flung flinging flingings flinger flingers",
	    "floe floes",
	    "fly flies flew flown flying flyings flier fliers flyer flyers",
	    "focus foci focuses focused focusing focusses focussed focussing focuser focal",
	    "foment foments fomented fomenting fomentings fomenter fomenters",
	    "foot feet",
	    "foot foots footed footing footer footers",
	    "footing footings footer footers",
	    "for",
	    "forbid forbids forbade forbidden forbidding forbiddings forbidder forbidders",
	    "foresee foresaw foreseen foreseeing foreseeings foreseer foreseers",
	    "forest forests forester foresting forestation forestations",
	    "forget forgets forgot forgotten forgetting forgettings forgetter "
	    +"forgetters forgetful",
	    "forsake forsakes forsook forsaken forsaking forsakings forsaker forsakers",
	    "found founds founded founding foundings founder founders",
	    "fragment fragments fragmented fragmenting fragmentings fragmentation "
	    +"fragmentations fragmenter fragmenters",
	    "free frees freer freest freed freeing freely freeness freenesses",
	    "freeze freezes froze frozen freezing freezings freezer freezers",
	    "from",
	    "full fully fuller fullest",
	    "fuller fullers full fulls fulled fulling fullings",
	    "fungus fungi funguses fungal",
	    "gallows",
	    "ganglion ganglia ganglions ganglionic",
	    "garment garments",
	    "gas gasses gassed gassing gassings gasser gassers",
	    "gas gases gasses gaseous gasless",
	    "gel gels gelled gelling gellings geller gellers",
	    //opt//"geneses genesis",
	    "german germans germanic germany German Germans Germanic Germany",
	    "get gets got gotten getting gettings getter getters",
	    "give gives gave given giving givings giver givers",
	    "gladiolus gladioli gladioluses gladiola gladiolas gladiolae",
	    "glans glandes",
	    //opt//"glissando glissandi glissandos",
	    "gluiness gluey glue glues glued gluing gluings gluer gluers",
	    //opt//"glum glummer glummest",
	    "go goes went gone going goings goer goers",
	    "godchild godchildren",
	    "good better best goodly goodness goodnesses",
	    "goods",
	    "goose geese",
	    "goose gooses goosed goosing goosings gooser goosers",
	    "grandchild grandchildren",
	    "grind grinds ground grinding grindings grinder grinders",
	    "ground grounds grounded grounding groundings grounder grounders groundless",
	    "grow grows grew grown growing growings grower growers growth",
	    "gum gums gummed gumming gummings gummer gummers",
	    "half halves",
	    "halve halves halved halving halvings halver halvers",
	    "hang hangs hung hanged hanging hangings hanger hangers",
	    //opt//"hanuman hanumans",
	    "have has had having havings haver havers",
	    "he him his himself",
	    "hear hears heard hearing hearings hearer hearers",
	    "here",
	    //opt//"herr herren Herr Herren",
	    //opt//"hiatus hiatuses",
	    "hide hides hid hidden hiding hidings hider hiders",
	    //opt//"hie hies hied hieing hying hieings hyings hier hiers",
	    "hippopotamus hippopotami hippopotamuses", 
	    "hold holds held holding holdings holder holders",
	    "honorarium honoraria honorariums",
	    "hoof hoofs hooves hoofed hoofing hoofer hoofers",
	    "how",
	    "hum hums hummed humming hummings hummer hummers",
	    "hymen hymens hymenal",
	    "hypotheses hypothesis hypothesize hypothesizes hypothesized hypothesizer "
	    +"hypothesizing hypothetical hypothetically",
	    "i",
	    //opt//"icosahedron icosahedra icosahedrons",
	    "if iffy",
	    "impediment impediments",
	    "implement implements implemented implementing implementings implementation "
	    +"implementations implementer implementers",
	    "imply implies implied implying implyings implier impliers",
	    "in inner",
	    "inclement",
	    "increment increments incremented incrementing incrementings incrementer "
	    +"incrementers incremental incrementally",
	    "index indexes indexed indexing indexings indexer indexers",
	    "index indexes indices indexical indexicals",
	    "indoor indoors",
	    "instrument instruments instrumented instrumenting instrumentings "
	    +"instrumenter instrumenters instrumentation instrumentations instrumental",
	    "integument integumentary",
	    "into",
	    "it its itself",
	    "july julys",
	    "keep keeps kept keeping keepings keeper keepers",
	    "knife knifes knifed knifing knifings knifer knifers",
	    "knife knives",
	    "know knows knew known knowing knowings knower knowers knowledge",
	    "lament laments lamented lamenting lamentings lamentation lamentations "
	    +"lamenter lamenters lamentable lamentably",
	    "larva larvae larvas larval",
	    "late later latest lately lateness",
	    "latter latterly",
	    "lay lays laid laying layer layers",
	    "layer layers layered layering layerings",
	    "lead leads led leading leadings leader leaders leaderless",
	    "leaf leafs leafed leafing leafings leafer leafers",
	    "leaf leaves leafless",
	    "leave leaves left leaving leavings leaver leavers",
	    //opt//"legume legumes",
	    "lend lends lent lending lendings lender lenders",
	    "less lesser least",
	    "let lets letting lettings",
	    //opt//"libretto librettos libretti",
	    "lie lies lay lain lying lier liers",
	    "lie lies lied lying liar liars",
	    "life lives lifeless",
	    "light lights lit lighted lighting lightings lightly lighter lighters "
	    +"lightness lightnesses lightless",
	    "likely likelier likeliest",
	    "limen limens",
	    "lineament lineaments",
	    "liniment liniments",
	    "live alive living",
	    "live lives lived living livings",
	    "liver livers",
	    //opt//"llamela llamelae",
	    "loaf loafs loafed loafing loafings loafer loafers",
	    "loaf loaves",
	    //opt//"locus loci",
	    "logic logics logical logically",
	    "lose loses lost losing loser losers loss losses",
	    "louse lice",
	    "lumen lumens",
	    "make makes made making makings maker makers",
	    "man mans manned manning mannings",
	    "man men",
	    "manly manlier manliest manliness manful manfulness manhood",
	    "manic manically",
	    "manner manners mannered mannerly mannerless mannerful",
	    //opt//"mantra mantras",
	    "many",
	    "matrix matrices matrixes",
	    "may might",
	    "maximum maxima maximums maximal maximize maximizes maximized maximizing",
	    "mean means meant meaning meanings meaningless meaningful",
	    "mean meaner meanest meanly meanness meannesses",
	    "median medians medianly medial",
	    "medium media mediums",
	    "meet meets met meeting meetings",
	    "memorandum memoranda memorandums",
	    "mere merely",
	    "metal metals metallic",
	    "might mighty mightily",
	    //opt//"milieu milieus milieux",
	    "millenium millennia milleniums millennial",
	    "mine mines mined mining minings miner miners",
	    "mine my our ours",
	    "minimum minima minimums minimal",
	    "minus minuses",
	    "miscellaneous miscellanea miscellaneously miscellaneousness miscellany",
	    //opt//"modulus moduli",
	    "molest molests molested molesting molestings molester molesters",
	    "moment moments",
	    "monument monuments monumental",
	    //opt//"mooncalf mooncalves",
	    "more most",
	    "mouse mice mouseless",
	    "much",
	    "multiply multiplies multiplier multipliers multiple multiples "
	    +"multiplying multiplyings multiplication multiplications",
	    "mum mums mummed mumming mummings mummer mummers",
	    "must musts",
	    "neither",
	    "nemeses nemesis",
	    "neurosis neuroses neurotic neurotics",
	    "nomen",
	    "none",
	    "nos no noes",
	    "not",
	    "nothing nothings nothingness",
	    "now",
	    "nowadays",
	    "nucleus nuclei nucleuses nuclear",
	    //opt//"nucleolus nucleoli nucleolar",
	    "number numbers numbered numbering numberings numberless",
	    "nutriment nutriments nutrient nutrients nutrition nutritions",
	    "oasis oases",
	    //opt//"octahedron octahedra octahedrons",
	    "octopus octopi octopuses",
	    "of",
	    "off",
	    "offer offers offered offering offerings offerer offerers offeror offerors",
	    "often",
	    "oftentimes",
	    "ointment ointments",
	    "omen omens",
	    "on",
	    "once",
	    "only",
	    //opt//"operetta operettas operetti",
	    "ornament ornaments ornamented ornamenting ornamentings ornamentation "
	    +"ornamenter ornamenters ornamental",
	    "outdoor outdoors",
	    "outlay outlays",
	    "outlie outlies outlay outlied outlain outlying outlier outliers",
	    "ovum ova",
	    "ox oxen",
	    //opt//"palazzo palazzi palazzos",
	    "parentheses parenthesis",
	    "parliament parliaments parliamentary",
	    "passerby passer-by passersby passers-by",
	    "past pasts",
	    //opt//"patois",
	    "pay pays paid paying payings payer payers payee payees payment payments",
	    //opt//"pediment pediments",
	    "per",
	    "perhaps",
	    "person persons people",
	    //opt//"phallus phalli phalluses phallic",
	    //opt//"phylum phyla phylums",
	    "phenomenon phenomena phenomenal",
	    //opt//"phosphorus",
	    "pi",
	    "picnic picnics picnicker picnickers picnicked picnicking picnickings",
	    "pigment pigments pigmented pigmenting pigmentings pigmenter pigmenters "
	    +"pigmentation pigmentations",
	    "please pleases pleased pleasing pleasings pleaser pleasers pleasure "
	    +"pleasures pleasuring pleasurings pleasant pleasantly pleasureless "
	    +"pleasureful",
	    "plus pluses plusses",
	    "polyhedra polyhedron polyhedral",
	    "priest priests priestly priestlier priestliest priestliness priestless",
	    "prognosis prognoses",
	    "prostheses prosthesis",
	    "prove proves proved proving provings proofs proof prover provers provable",
	    "psychosis psychoses psychotic psychotics",
	    //opt//"pupa pupae pupas pupal",
	    "qed",
	    "quiz quizzes quizzed quizzing quizzings quizzer quizzers",
	    "raiment",
	    "rather",
	    "re",
	    "real really",
	    "redo redoes redid redone redoing redoings redoer redoers",
	    "regiment regiments regimented regimenting regimenter regimenters "
	    +"regimentation regimental",
	    "rendezvous",
	    "requiz requizzes requizzed requizzing requizzings requizzer requizzers",
	    "ride rides rode ridden riding ridings rider riders rideless",
	    "ring rings rang rung ringing ringings ringer ringers ringless",
	    "rise rises rose risen rising risings riser risers",
	    //opt//"roof roofs roofed roofing roofings roofer roofers roofless",
	    "rose roses",
	    "rudiment rudiments rudimentary",
	    "rum rums rummed rumming rummings rummer rummers",
	    "run runs ran running runnings runner runners",
	    "sacrament sacraments sacramental",
	    "same sameness",
	    "sans",
	    "saw saws sawed sawn sawing sawings sawyer sawyers",
	    "say says said saying sayings sayer sayers",
	    "scarf scarfs scarves scarfless",
	    "schema schemata schemas",
	    //opt//"scherzo scherzi scherzos",
	    //opt//"scrotum scrota scrotums",
	    "sediment sediments sedimentary sedimentation sedimentations",
	    "see sees saw seen seeing seeings seer seers",
	    "seek seeks sought seeking seekings seeker seekers",
	    "segment segments segmented segmenting segmentings segmenter segmenters "
	    +"segmentation segmentations",
	    "self selves selfless",
	    "sell sells sold selling sellings seller sellers",
	    "semen",
	    "send sends sent sending sendings sender senders",
	    "sentiment sentiments sentimental",
	    //opt//"seraph seraphs seraphim",
	    "series",
	    "set sets setting settings",
	    "several severally",
	    "sew sews sewed sewn sewing sewings sewer sewers",
	    "sewer sewers sewerless",
	    "shake shakes shook shaken shaking shakings shaker shakers",
	    "shall should",
	    "shaman shamans",
	    "shave shaves shaved shaven shaving shavings shaver shavers shaveless",
	    "she her hers herself",
	    "sheaf sheaves sheafless",
	    "sheep",
	    "shelf shelves shelved shelfing shelvings shelver shelvers shelfless",
	    "shine shines shined shone shining shinings shiner shiners shineless",
	    "shoe shoes shoed shod shoeing shoeings shoer shoers shoeless",
	    "shoot shoots shot shooting shootings shooter shooters",
	    "shot shots",
	    "show shows showed shown showing showings shower showers",
	    "shower showers showery showerless",
	    "shrink shrinks shrank shrunk shrinking shrinkings shrinker shrinkers "
	    +"shrinkable",
	    "sideways",
	    //opt//"simplex simplexes simplices",
	    "simply simple simpler simplest",
	    "since",
	    "sing sings sang sung singing singings singer singers singable",
	    "sink sinks sank sunk sinking sinkings sinker sinkers sinkable",
	    "sit sits sat sitting sittings sitter sitters",
	    "ski skis skied skiing skiings skier skiers skiless skiable",
	    "sky skies",
	    "slay slays slew slain slaying slayings slayer slayers",
	    "sleep sleeps slept sleeping sleepings sleeper sleepers sleepless",
	    "so",
	    "some",
	    "something",
	    "sometime sometimes",
	    "soon",
	    "spa spas",
	    "speak speaks spoke spoken speaking speakings speaker speakers",
	    "species specie",
	    "spectrum spectra spectrums",
	    "speed speeds sped speeded speeding speedings speeder speeders",
	    "spend spends spent spending spendings spender spenders spendable",
	    "spin spins spun spinning spinnings spinner spinners",
	    "spoke spokes",
	    "spring springs sprang sprung springing springings springer springers "
	    +"springy springiness",
	    "staff staffs staves staffed staffing staffings staffer staffers",
	    "stand stands stood standing standings",
	    "stasis stases",
	    "steal steals stole stolen stealing stealings stealer stealers",
	    "stick sticks stuck sticking stickings sticker stickers",
	    "stigma stigmata stigmas stigmatize stigmatizes stigmatized stigmatizing",
	    "stimulus stimuli",
	    "sting stings stung stinging stingings stinger stingers",
	    "stink stinks stank stunk stinking stinkings stinker stinkers",
	    "stomach stomachs",
	    "stratum strata stratums",
	    "stride strides strode stridden striding stridings strider striders",
	    "string strings strung stringing stringings stringer stringers stringless",
	    "strive strives strove striven striving strivings striver strivers",
	    "strum strums strummed strumming strummings strummer strummers strummable",
	    //opt//"stylus styli styluses",
	    //opt//"succubus succubi",
	    "such",
	    "suffer suffers suffered suffering sufferings sufferer sufferers sufferable",
	    "suggest suggests suggested suggesting suggestings suggester suggesters "
	    +"suggestor suggestors suggestive suggestion suggestions suggestible "
	    +"suggestable",
	    "sum sums summed summing summings summer summers",
	    "summer summers summered summering summerings",
	    "supplement supplements supplemented supplementing supplementings "
	    +"supplementation supplementer supplementers supplementary supplemental",
	    "supply supplies supplied supplying supplyings supplier suppliers",
	    "swear swears swore sworn swearing swearings swearer swearers",
	    "sweep sweeps swept sweeping sweepings sweeper sweepers",
	    "swell swells swelled swollen swelling swellings",
	    "swim swims swam swum swimming swimmings swimmer swimmers swimable",
	    "swine",
	    "swing swings swung swinging swingings swinger swingers",
	    "syllabus syllabi syllabuses",
	    "symposium symposia symposiums",
	    "synapse synapses",
	    "synapsis synapses",
	    "synopsis synopses",
	    "synthesis syntheses",
	    "tableau tableaux tableaus",
	    "take takes took taken taking takings taker takers takable",
	    "teach teaches taught teaching teachings teacher teachers teachable",
	    "tear tears tore torn tearing tearings tearer tearers tearable",
	    "tegument teguments",
	    "tell tells told telling tellings teller tellers tellable",
	    "temperament temperaments temperamental temperamentally",
	    "tenement tenements",
	    //opt//"terminus termini",
	    //opt//"than thans",
	    //opt//"that thats",
	    "the",
	    "there theres",
	    "theses thesis",
	    "they them their theirs themselves",
	    "thief thieves thieving thievings",
	    "think thinks thought thinking thinker thinkers thinkable",
	    "this that these those",
	    "thought thoughts thougtful thoughtless",
	    "throw throws threw thrown throwing throwings thrower throwers throwable",
	    //opt//"thus thuses",
	    "tic tics",
	    "tie ties tied tying tyings tier tiers tieable tieless",
	    "tier tiers tiered tiering tierings tierer tierers",
	    "to",
	    "toe toes toed toeing toeings toer toers toeless",
	    "together togetherness",
	    "too",
	    "tooth teeth toothless",
	    "topaz topazes",
	    "torment torments tormented tormenting tormentings tormenter tormenters "
	    +"tormentable",
	    "toward towards",
	    "tread treads trod trodden treading treadings treader treaders",
	    "tread treads treadless retread retreads",
	    "true truly trueness",
	    "two twos",
	    "u",
	    "under",
	    "underlay underlays underlaid underlaying underlayings underlayer "
	    +"underlayers",
	    "underlie underlies underlay underlain underlying underlier underliers",
	    "undo undoes undid undone undoing undoings undoer undoers undoable",
	    "unrest unrestful",
	    "until",
	    "unto",
	    "up",
	    "upon",
	    "upstairs",
	    "use uses user users used using useful useless",
	    "various variously",
	    "vehement vehemently vehemence",
	    "versus",
	    "very",
	    "visit visits visited visiting visitings visitor visitors",
	    "vortex vortexes vortices",
	    "wake wakes woke waked woken waking wakings waker wakers wakeful "
	    +"wakefulness wakefulnesses wakeable",
	    "wear wears wore worn wearing wearings wearer wearers wearable",
	    "weather weathers weathered weathering weatherly",
	    "weave weaves wove woven weaving weavings weaver weavers weaveable",
	    "weep weeps wept weeping weepings weeper weepers",
	    "wharf wharfs wharves",
	    //opt//"what whats",
	    //opt//"when whens",
	    "where wheres",
	    "whereas whereases",
	    "whether whethers",
	    //opt//"which whiches",
	    "while whiles whilst whiled whiling",
	    "whiz whizzes whizzed whizzing whizzings whizzer whizzers",
	    "who whom whos whose whoses",
	    "why whys",
	    "wife wives wifeless",
	    "will wills willed willing willings willful",
	    "will would",
	    "win wins won winning winnings winner winners winnable",
	    "wind winds wound winding windings winder winders windable",
	    "wind winds windy windless",
	    "with",
	    "within",
	    "without",
	    "wolf wolves",
	    "woman women womanless womanly",
	    "wound wounds wounded wounding woundings",
	    "write writes wrote written writing writings writer writers writeable",
	    "yeses yes",
	    "yet yets",
	    "you your yours yourself"};

	initialize(exceptionTable);

	// The rules:

	// For words ending in -s (s-rules):

	Rule[] sRules = {
	    r(".aeiouy bcdfghjklmnpqrstvwxyz + i n e s s",
	      "y,ies,ier,iers,iest,ied,ying,yings,ily,inesses,iment,iments,iless,iful",
	      this),
	    // 	(e.g., happiness, business)

	    r(".aeiouy + e l e s s",
	      "e,es,er,ers,est,ed,ing,ings,eing,eings,ely,eness,enesses,ement,ements,"
	      +"eness,enesses,eful",
	      this),
	    // 	(e.g., baseless, shoeless)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdgklmnprt + l e s s",
	      "_,s,&er,&ers,&est,&ed,&ing,&ings,ly,ness,nesses,ment,ments,ful",
	      this),
	    // 	(e.g., gutless, hatless, spotless)

	    r(".aeiouy + l e s s",
	      "_,s,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,ful",
	      this),
	    // 	(e.g., thoughtless, worthless)

	    r(".aeiouy + e n e s s",
	      "e,es,er,ers,est,ed,ing,ings,eing,eings,ely,enesses,ement,ements,"
	      +"eless,eful",
	      this),
	    // 	(e.g., baseness, toeness)

	    r(".aeiouy + n e s s",
	      "_,s,er,ers,est,ed,ing,ings,ly,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., bluntness, grayness)

	    r(".aeiouy s s +",
	      "es,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., albatross, kiss)

	    r(".aeiouy o u s +",
	      "ly,ness",
	      this),
	    // 	(e.g., joyous, fractious, gaseous)

	    r("+ i e s",
	      "y,ie,yer,yers,ier,iers,iest,ied,ying,yings,yness,iness,ieness,ynesses,"
	      +"inesses,ienesses,iment,iement,iments,iements,yless,iless,ieless,yful,"
	      +"iful,ieful",
	      this),
	    // 	(e.g., tries, unties, jollies, beauties)

	    r(".aeiouy + s i s",
	      "ses,sises,sisness,sisment,sisments,sisless,sisful",
	      this),
	    // 	(e.g., crisis, kinesis)

	    r(".aeiouy i s +",
	      "es,ness,ment,ments,less,ful",
	      this),
	    // 	(e.g., bronchitis, bursitis)

	    r(".aeiouy c h + e s",
	      "_,e,er,ers,est,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,"
	      +"ement,ments,ements,less,eless,ful,eful",
	      this),

	    r(".aeiouy s h + e s",
	      "_,e,er,ers,est,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,"
	      +"ement,ments,ements,less,eless,ful,eful",
	      this),

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i z e s",
	      "ize,izer,izers,ized,izing,izings,ization,izations,"
	      +"ise,ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenizes) // adds British variations

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i s e s",
	      "ize,izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ise,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenises) // British variant  // ~expertise

	    r(".aeiouy jsxz + e s",
	      "_,e,er,ers,est,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,"
	      +"ement,ments,ements,less,eless,ful,eful",
	      this),
	    // 	(e.g., aches, arches)
	    //  v is not in this set because ve words can keep the e before ing
	    //  NB: we are not handling -is plurals like crises

	    r(".aeiouy d g + e s",
	      "e,er,ers,est,ed,ing,ings,ely,eness,enesses,ment,ments,ement,ements,"
	      +"eless,eful",
	      this),
	    // 	(e.g., judges, abridges)

	    r("e + s",
	      "*_",
	      this),
	    // 	(e.g., trees, races, likes, agrees)
	    //  covers all other -es words

	    r("s e g m e n t + s",
	      "*_",
	      this),
	    // 	(e.g., segments, bisegments, cosegments)

	    r("p i g m e n t + s",
	      "*_",
	      this),
	    // 	(e.g., pigments, depigments, repigments)

	    r(".aeiouy d g + m e n t s",
	      "ment,*ment",
	      this),
	    // 	(e.g., judgments, abridgments)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz i m e n t + s",
	      "*_",
	      this),
	    // 	(e.g., merriments, embodiments)
	    //  -iment in turn will generate y and *y (redo y)

	    r(".aeiouy m e n t + s",
	      "*_",
	      this),
	    // 	(e.g., atonements, entrapments)

	    r(".aeiouy e r + s",
	      "*_",
	      this),
	    // 	(e.g., viewers, meters, traders, transfers)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz aeiouy bdglmnprt + s",
	      "*_",
	      this),
	    // 	(e.g., unflags)
	    //  polysyllables

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdglmnprt + s",
	      "*_",
	      this),
	    // 	(e.g., frogs)
	    //  monosyllables

	    r(".aeiouy i n g + s",
	      "*_",
	      this),
	    // 	(e.g., killings, muggings)

	    r(".aeiouy l l + s",
	      "*_",
	      this),
	    // 	(e.g., hulls, tolls)

	    r(".bcdfghjklmnpqrstvwxyz + s",
	      "*_",
	      this),
	    // 	(e.g., beads, toads, zoos)
	};

	// For words ending in -e (e-rules):

	Rule[] eRules = {
	    r(".aeiouy bcdfghjklmnpqrstvwxyz l + e",
	      "es,er,ers,est,ed,ing,ings,y,ely,eness,enesses,ement,ements,eless,eful",
	      this),
	    // 	(e.g., able, abominable, fungible, table, enable, idle, subtle)

	    r("+ i e",
	      "ies,ier,iers,iest,ied,ying,yings,iely,ieness,ienesses,iement,iements,"
	      +"ieless,ieful",
	      this),
	    // 	(e.g., bookie, magpie, vie)

	    r("y e +",
	      "s,r,rs,st,d,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., dye, redye, redeye)

	    r(".aeiouy d g + e",
	      "es,er,ers,est,ed,ing,ings,ely,eness,enesses,ment,ments,less,ful,ement,"
	      +"ements,eless,eful",
	      this),
	    // 	(e.g., judge, abridge)

	    r("u + e",
	      "es,er,ers,est,ed,ing,ings,eing,eings,ly,ely,eness,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful",
	      this),
	    // 	(e.g., true, due, imbue)

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i z e",
	      "izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ise,ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenize) // adds British variations

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i s e",
	      "ize,izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenise) // British variant  // ~expertise

	    r("+ e",
	      "es,er,ers,est,ed,ing,ings,eing,eings,ely,eness,enesses,ement,ements,"
	      +"eless,eful",
	      this),
	    // 	(e.g., tree, agree, rage, horse, hoarse)
	};

	// For words ending in -ed (ed-rules):
  
	Rule[] edRules = {
	    r("r e e + d",
	      "ds,der,ders,ded,ding,dings,dly,dness,dnesses,dment,dments,dless,dful,*_",
	      this),
	    // 	(e.g., agreed, freed, decreed, treed)

	    r("e e + d",
	      "ds,der,ders,ded,ding,dings,dly,dness,dnesses,dment,dments,dless,dful",
	      this),
	    // 	(e.g., feed, seed, Xweed)

	    r("bcdfghjklmnpqrstvwxyz + i e d",
	      "y,ie,ies,ier,iers,iest,ying,yings,ily,yly,iness,yness,inesses,ynesses,"
	      +"iment,iments,iless,iful,yment,yments,yless,yful",
	      this),
	    // 	(e.g., tried)

	    r(".aeiouy .bcdfghjklmnpqrstvwxyz l + l e d",
	      "_,s,er,ers,est,ing,ings,ly,ness,nesses,ment,ments,less,ful,&,&s,"
	      +"&er,&ers,&est,&ing,&ings,&y,&ness,&nesses,&ment,&ments,&ful",
	      this),
	    // 	(e.g., controlled, fulfilled, rebelled)
	    //  both double and single l forms

	    r(".aeiouy l + l e d",
	      "&,&s,&er,&ers,&est,&ing,&ings,&y,&ness,&nesses,&ment,&ments,&ful",
	      this),
	    // 	(e.g., pulled, filled, fulled)

	    r(".aeiouy s + s e d",
	      "&,&es,&er,&ers,&est,&ing,&ings,&ly,&ness,&nesses,&ment,&ments,&less,&ful",
	      this),
	    // 	(e.g., hissed, grossed)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdgklmnprt + & e d",
	      "_,s,&er,&ers,&est,&ing,&ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., hugged, trekked)

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i z e d",
	      "izes,izer,izers,ize,izing,izings,ization,izations,"
	      +"ise,ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenize) // adds British variations

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i s e d",
	      "ize,izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ises,iser,isers,ise,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenise) // British variant  // ~expertise

	    r(".aeiouy + e d",
	      "_,e,s,es,er,ers,est,ing,ings,ly,ely,ness,eness,nesses,enesses,"
	      +"ment,ement,ments,ements,less,eless,ful,eful",
	      this),
	    // 	(e.g., spoiled, tooled, tracked, roasted, atoned, abridged)

	    r("e d +",
	      "s,&er,&ers,&est,&ed,&ing,&ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., bed, sled)
	    //  words with a single e as the only vowel
	};

	// For words ending in -er (er-rules):

	Rule[] erRules = {
	    r("m e t e r +",
	      "s,er,ers,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., altimeter, ammeter, odometer, perimeter)

	    r("+ e e r",
	      "eers,eered,eering,eerings,eerly,eerness,eernesses,eerment,eerments,"
	      +"eerless,eerful,ee,ees,eest,eed,eeing,eeings,eely,eeness,eenesses,"
	      +"eement,eements,eeless,eeful,eerer,eerers,eerest",
	      this),
	    // 	(e.g., agreer, beer, budgeteer, engineer, freer)

	    r("bcdfghjklmnpqrstvwxyz + i e r",
	      "y,ie,ies,iest,ied,ying,yings,ily,yly,iness,yness,inesses,ynesses,"
	      +"yment,yments,yless,yful,iment,iments,iless,iful,iers,iered,"
	      +"iering,ierings,ierly,ierness,iernesses,ierment,ierments,"
	      +"ierless,ierful,ierer,ierers,ierest",
	      this),
	    // 	(e.g., acidifier, tier)

	    r(".aeiouy l + l e r",
	      "&,&s,&est,&ed,&ing,&ings,ly,lely,&ness,&nesses,&ment,&ments,&ful,"
	      +"&ers,&ered,&ering,&erings,&erly,&erness,&ernesses,&erments,"
	      +"&erless,&erful",
	      this),
	    // 	(e.g., puller, filler, fuller)
	    //  did not add: &erer, &erest, &errer, &errers, &errest,
	    //  since i think the &er here is always an ending (?)

	    r(".aeiouy s + s e r",
	      "&,&es,&est,&ed,&ing,&ings,&ly,&ness,&nesses,&ment,&ments,&less,&ful,"
	      +"&ers,&ered,&ering,&erings,&erly,&erness,&ernesses,&erment,&erments,"
	      +"&erless,&erful",
	      this),
	    // 	(e.g., hisser, grosser)
	    //  did not add: &erer, &erest, &errer, &errers, &errest,
	    //  since i think the &er here is always an ending (?)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdgkmnprt + & e r",
	      "_,s,&est,&ed,&ing,&ings,ly,ness,nesses,ment,ments,less,ful,&ers,&ered,"
	      +"&ering,&erings,&erly,&erness,&ernesses,&erments,&erless,&erful",
	      this),
	    // 	(e.g., bigger, trekker, hitter)
	    //  did not add: &erer, &erest, &errer, &errers, &errest,
	    //  since i think the &er here is always an ending (?)

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i z e r",
	      "izes,ize,izers,ized,izing,izings,ization,izations,"
	      +"ise,ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenize) // adds British variations

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i s e r",
	      "ize,izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ises,ise,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenise) // British variant  // ~expertise

	    r(".aeiouy + e r",
	      "_,e,s,es,est,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful,ers,ered,erred,ering,erring,erings,"
	      +"errings,erly,erness,ernesses,erment,erments,erless,erful,erer,erers,"
	      +"erest,errer,errers,errest",
	      this),
	    // 	(e.g., actioner, atoner, icer, trader, accruer, churchgoer, prefer)
	};

	// For words ending in -est (est-rules):

	Rule[] estRules = {
	    r("bcdfghjklmnpqrstvwxyz + i e s t",
	      "y,ies,ier,iers,ied,ying,yings,ily,yly,iness,yness,inesses,ynesses,"
	      +"iment,iments,iless,iful",
	      this),
	    // 	(e.g., sliest, happiest, wittiest)

	    r(".aeiouy l + l e s t",
	      "&,&s,&er,&ers,&ed,&ing,&ings,ly,&ness,&nesses,&ment,&ments,&ful",
	      this),
	    // 	(e.g., fullest)

	    r(".aeiouy s + s e s t",
	      "&,&es,&er,&ers,&ed,&ing,&ings,&ly,&ness,&nesses,&ment,&ments,&less,&ful",
	      this),
	    // 	(e.g.,  grossest)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdglmnprst + & e s t",
	      "_,s,&er,&ers,&ed,&ing,&ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., biggest)

	    r(".aeiouy c h + e s t",
	      "e,es,er,ers,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful,ests,ester,esters,ested,"
	      +"esting,estings,estly,estness,estnesses,estment,estments,estless,estful",
	      this),

	    r(".aeiouy s h + e s t",
	      "e,es,er,ers,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful,ests,ester,esters,ested,"
	      +"esting,estings,estly,estness,estnesses,estment,estments,estless,estful",
	      this),

	    r(".aeiouy jxsz + e s t",
	      "e,es,er,ers,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful,ests,ester,esters,ested,"
	      +"esting,estings,estly,estness,estnesses,estment,estments,estless,estful",
	      this),
	    // 	(e.g., basest, archest, rashest)

	    r("e r + e s t",
	      "e,es,er,ers,ed,eing,eings,ely,eness,enesses,ement,ements,eless,eful,"
	      +"ests,ester,esters,ested,esting,estings,estly,estness,estnesses,"
	      +"estment,estments,estless,estful",
	      this),
	    // 	(e.g., severest, Xinterest, merest)

	    r(".aeiouy + e s t",
	      "_,e,s,es,er,ers,ed,ing,ings,ly,ely,ness,eness,nesses,enesses,ment,ments,"
	      +"less,ful,ement,ements,eless,eful,ests,ester,esters,ested,esting,estings,"
	      +"estly,estness,estnesses,estment,estments,estless,estful",
	      this),
	    // 	(e.g., slickest, coolest, ablest, amplest, protest, quest)
	    //  polysyllables - note that this picks up quest because of the
	    //  u but the forms generated are harmless

	    r("e s t +",
	      "s,er,ers,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., rest, test)
	    //  monosyllables
	};
  
	// For words ending in -ful (ful-rules ):

	Rule[] fulRules = {
	    r(".aeiouy bcdfghjklmnpqrstvwxyz + i f u l",
	      "ifully,ifulness,*y",
	      this),
	    // 	(e.g., beautiful, plentiful)

	    r(".aeiouy + f u l",
	      "fully,fulness,*_",
	      this),
	    // 	(e.g., hopeful, sorrowful)
	};
  
	// For words ending in -ical:

	Rule[] icalRules = {
	    r(".aeiouy + i c a l",
	      "ic,ics,ically",
	      this),
	    // 	(e.g., academical, electrical, theatrical)
	};

	// For words ending in -ic:

	Rule[] icRules = {
	    r(".aeiouy + i c",
	      "ics,ical,ically",
	      this),
	    // 	(e.g., academic, clinic, mechanic, methodic)
	    //  we're now booting on all redos (w/ or w/out e) as too risky
	};
  
	// For words ending in -ing (ing-rules):

	Rule[] ingRules = {
	    r("bcdfghjklmnpqrstvwxyz + y i n g",
	      "yings,ie,y,ies,ier,iers,iest,ied,iely,yly,ieness,yness,ienesses,ynesses,"
	      +"iment,iments,iless,iful",
	      this),
	    // 	(e.g., dying, crying, supplying)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r(".aeiouy l + l i n g",
	      "*_,&,&s,&er,&ers,&est,&ed,&ings,&ness,&nesses,&ment,&ments,&ful",
	      this),
	    // 	(e.g., pulling, filling, fulling)
	    //  did not add -ing forms (see last 8 ing-rules)
	    //  note that -ly has been dropped from this rule, as i believe
	    //  that only a verb can be the stem here

	    r(".aeiouy s + s i n g",
	      "&,&s,&er,&ers,&est,&ed,&ings,&ly,&ness,&nesses,&ment,&ments,&less,&ful",
	      this),
	    // 	(e.g., hissing, grossing, processing)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdgklmnprt + & i n g",
	      "_,s,&er,&ers,&est,&ed,&ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., hugging, trekking)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r("+ e e i n g",
	      "ee,ees,eer,eers,eest,eed,eeings,eely,eeness,eenesses,eement,eements,"
	      +"eeless,eeful",
	      this),
	    // 	(e.g., freeing, agreeing)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r("+ e i n g",
	      "e,es,er,ers,est,ed,eings,ely,eness,enesses,ement,ements,eless,eful",
	      this),
	    // 	(e.g., ageing, aweing)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r("aeiou y + i n g",
	      "_,s,er,ers,est,ed,ings,ly,ingly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., toying, playing)
	    //  did not add -ing forms (see last 8 ing-rules)

	    r(".aeiou bcdfghjklmnpqrstvwxyz eio t + i n g",
	      "*_,*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    //  (e.g., editing, crediting, expediting, siting, exciting
	    //  deleting, completing, coveting, interpreting
	    //  piloting, pivoting, devoting)

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdgklmt + i n g",
	      "*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., robing, siding, doling, translating, flaking)
	    //  these consonants would double if there were no e in the stem
	    //  removed p from this group: developing, galloping, etc.
	    //  removed r from this group: monitoring, coloring, motoring
	    //  removed n from this group: happening

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i z i n g",
	      "izes,izer,izers,ized,ize,izings,ization,izations,"
	      +"ise,ises,iser,isers,ised,ising,isings,isation,isations",
	      this),
	    // 	(e.g., tokenize) // adds British variations

	    r(".aeiouy bcdfghjklmnpqrstvwxz + i s i n g",
	      "ize,izes,izer,izers,ized,izing,izings,ization,izations,"
	      +"ises,iser,isers,ised,ise,isings,isation,isations",
	      this),
	    // 	(e.g., tokenise) // British variant  // ~expertise

	    r("aeiouy cgsvz + i n g",
	      "*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., icing, aging, achieving, amazing, housing)
	    //  we had k in this list, but that's only right if the vowel
	    //  is single - looking needs to be redone with _

	    r("bcdfghjklmnpqrstvwxyz clsuv + i n g",
	      "*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., dancing, troubling, arguing, bluing, carving)

	    r("lr g + i n g",
	      "*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., charging, bulging)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz bdfjkmnpqrtwxz + i n g",
	      "*_,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., farming, harping, interesting, bedspring, redwing)

	    r(".aeiouy + i n g",
	      "*_,*e,ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // (e.g., spoiling, reviling, autoing, egging, hanging, hingeing,
	    // judging, breathing, mothing)
	    // these are the cases where we can't tell if e is needed or not

	    r("+ i n g",
	      "ings,inger,ingers,ingest,inged,inging,ingings,ingly,"
	      +"ingness,ingnesses,ingment,ingments,ingless,ingful",
	      this),
	    // 	(e.g., wing, thing)
	    //  monosyllables
	};
  
	// For words ending in -leaf (leaf-rules):

	Rule[] leafRules = {
	    r("+ l e a f",
	      "leaves",
	      this),
	    // 	(e.g., cloverleaf, flyleaf)
	    //  leaf itself is in the exceptions list
	};

	// For words ending in -man:

	Rule[] manRules = {
	    r("+ m a n",
	      "men,mans,maner,manner,manners,maners,manest,maned,manned,maning,"
	      +"manning,manings,mannings,manly,manness,mannesses,manless,manful",
	      this),
	    // 	(e.g., woman, policeman, cayman, unman)
	};

	// For words ending in -men:

	Rule[] menRules = {
	    r("+ m e n",
	      "man,mens,mener,meners,menest,mened,mening,menings,menly,"
	      +"menness,mennesses,menless,menful",
	      this),
	    // 	(e.g., policewomen, hatchetmen, dolmen)
	};
  
	// For words ending in -ment (ment-rules):

	Rule[] mentRules = {
	    r("s e g m e n t +",
	      "s,ed,ing,ings,er,ers,ly,ness,nesses,less,ful",
	      this),
	    // 	(e.g., segment, bisegment, cosegment)

	    r("p i g m e n t +",
	      "s,ed,ing,ings,er,ers,ly,ness,nesses,less,ful",
	      this),
	    // 	(e.g., pigment, depigment, repigment)

	    r(".aeiouy d g + m e n t",
	      "*e",
	      this),
	    // 	(e.g., judgment, abridgment)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz + i m e n t",
	      "*y",
	      this),
	    // 	(e.g., merriment, embodiment)

	    r(".aeiouy + m e n t",
	      "*_",
	      this),
	    // 	(e.g., atonement, entrapment)
	};
  
	// For words ending in -o (o-rules):

	Rule[] oRules = {
	    r("aeiou o +",
	      "s,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., taboo, rodeo)
	    //  no need to generate an es form

	    r(".aeiouy o +",
	      "s,es,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., tomato, bonito)
	    //  make both s and es plurals
	};
  
	// For words ending in -um (um-rules):

	Rule[] umRules = {
	    r("+ u m",
	      "a,ums,umer,ummer,umers,ummers,umed,ummed,uming,umming,umings,"
	      +"ummings,umness,umments,umless,umful",
	      this),
	    // 	(e.g., datum, quantum, tedium, strum, [oil]drum, vacuum)
	};

	// For words ending in -y (y-rules):

	Rule[] yRules = {
	    r(".aeiouy b + l y",
	      "le,les,ler,lers,lest,led,ling,lings,leness,lenesses,lement,lements,"
	      +"leless,leful",
	      this),
	    // 	(e.g., ably, horribly, wobbly)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz + i l y",
	      "y,ies,ier,iers,iest,ied,ying,yings,yness,iness,ynesses,inesses,"
	      +"iment,iments,iless,iful",
	      this),
	    // 	(e.g., happily, dizzily)

	    r(".aeiouy f u l + l y",
	      "*_",
	      this),
	    // 	(e.g., peaceful+ly)

	    r(".aeiouy l + l y",
	      "*_,lies,lier,liers,liest,lied,lying,lyings,liness,linesses,"
	      +"liment,liments,liless,liful,*l",
	      this),
	    // 	(e.g., fully, folly, coolly, fatally, dally)
	    //  both -l and -ll words

	    r("aou + l y",
	      "lies,lier,liers,liest,lied,lying,lyings,liness,linesses,"
	      +"liment,liments,liless,liful",
	      this),
	    // 	(e.g., monopoly, Xcephaly, holy)
	    //  i.e., treat ly as part of the stem

	    r("+ l y",
	      "*_,lies,lier,liers,liest,lied,lying,lyings,liness,linesses,lyless,lyful",
	      this),
	    // 	(e.g., frequently, comely, deeply, apply, badly)
	    //  i.e., redo stem AND generate all forms with ly as part of stem

	    r("bcdfghjklmnpqrstvwxyz + y",
	      "ies,ier,iers,iest,ied,ying,yings,ily,yness,iness,ynesses,inesses,"
	      +"iment,iments,iless,iful,yment,yments,yless,yful",
	      this),
	    // 	(e.g., happy, spy, cry)

	    r("aeiou y +",
	      "s,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., betray, gay, stay)
	};

	// For other words (root-rules):
  
	Rule[] rootRules = {
	    r(".aeiouy c h +",
	      "es,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),

	    r(".aeiouy s h +",
	      "es,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),

	    r(".aeiouy jxz +",
	      "es,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., fix, arch, rash)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz aeiouy bdglmnprt +",
	      "s,er,ers,est,ed,ing,ings,&er,&ers,&est,&ed,&ing,&ings,ly,"
	      +"ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., unflag, open, besot)
	    //  both possibilities for multisyllables

	    r("bcdfghjklmnpqrstvwxyz aeiouy bdglmnprt +",
	      "s,&er,&ers,&est,&ed,&ing,&ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., bed, cop)

	    r(".aeiouy bcdfghjklmnpqrstvwxyz aeiouy m a + t a",
	      "_, s,tas,tum,tums,ton,tons,tic,tical",
	      this),
	    // 	(e.g., schemata, automata)

	    r(".aeiouy t + a",
	      "as,ae,um,ums,on,ons,ic,ical",
	      this),
	    // 	(e.g., chordata, data, errata, sonata, toccata)

	    r(".aeiouy .bcdfghjklmnpqrstvwxyz + a",
	      "as,ae,ata,um,ums,on,ons,al,atic,atical",
	      this),
	    // 	(e.g., schema, ova, polyhedra)

	    r(".aeiouy l l +",
	      "s,er,ers,est,ed,ing,ings,y,ness,nesses,ment,ments,-less,ful",
	      this),
	    // 	(e.g., full)
	    //  note: adds a hyphen before less

	    r(".aeiouy +",
	      "s,er,ers,est,ed,ing,ings,ly,ness,nesses,ment,ments,less,ful",
	      this),
	    // 	(e.g., spoon, rhythm)
	};

	rulesTable = new Hashtable();
	rulesTable.put("s", sRules);
	rulesTable.put("e", eRules);
	rulesTable.put("ed", edRules);
	rulesTable.put("er", erRules);
	rulesTable.put("est", estRules);
	rulesTable.put("ful", fulRules);
	rulesTable.put("ic", icRules);
	rulesTable.put("ical", icalRules);
	rulesTable.put("ing", ingRules);
	rulesTable.put("man", manRules);
	rulesTable.put("men", menRules);
	rulesTable.put("ment", mentRules);
	rulesTable.put("leaf", leafRules);
	rulesTable.put("o", oRules);
	rulesTable.put("um", umRules);
	rulesTable.put("y", yRules);
	rulesTable.put("default", rootRules);
    }

  
}
