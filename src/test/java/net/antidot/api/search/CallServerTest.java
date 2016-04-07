package net.antidot.api.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.antidot.api.common.Service;
import net.antidot.api.common.Status;
import net.antidot.api.surcharge.FilterCoderLocal;
import net.antidot.protobuf.reply.Reply.replies;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

public class CallServerTest {

	@Test
	public void testCallServer() {
		// Register facets
		/** [Facets and Facet manager] */
		FacetRegistry facetRegistry = new FacetRegistry();
		facetRegistry.addFacet(new Facet("domain", FacetType.STRING, FacetStickyness.NON_STICKY));
		facetRegistry.addFacet(new Facet("nb_words", FacetType.INTEGER));
		facetRegistry.addFacet(new Facet("publication_date", FacetType.DATE));

		/** [Facets and Facet manager] */

		// Initialize connector and query manager
		/** [Connector] */
		Connector connector = new Connector("v75-dev02.vitry.exploit.anticorp",
				new Service(60002,Status.ALPHA));
		/** [Connector] */

		// Decode query from request parameters
		/** [Coder/decoder and Query] */
		FilterCoderLocal filterCoderLocal = new FilterCoderLocal();
		CoderManager coderMgr = new CoderManager(filterCoderLocal);

		// http://eval.partners.antidot.net/search?afs:service=70000
		QueryCoder queryCoder = new QueryCoder(
				"http://v75-dev02.vitry.exploit.anticorp", coderMgr);
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("feed", new String[] { "CANLII" });
		parameters
				.put("filter", new String[] { "domain=Droits_et_libertés" });
		parameters
		.put("filter", new String[] { "publication_date=1984-11" });
		Query query = queryCoder.decode(parameters);
		/** [Coder/decoder and Query] */

		/** [Query manager] */
		QueryManager queryManager = new QueryManager(connector, facetRegistry);
		byte[] reply = null;
		try {
			// Query AFS search engine
			reply = queryManager.send(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * try { String str = new String(reply, "UTF-8");
		 * System.out.println(str);
		 * 
		 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		/** [Query manager] */

		/** [Replies helper] */

		replies result = null;
		try {
			result = replies.parseFrom(reply,
					ProtobufExtensionManager.getResgistry());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		RepliesHelper helper = new RepliesHelper(result, facetRegistry,
				queryCoder, query);

		while (helper.hasNext()) {
			ReplySetHelper replySetHelper = helper.next();

			System.out.print("replySetHelper = "
					+ replySetHelper.getMeta().getFeedName());
			System.out.println("");
			System.out.println(replySetHelper.getReplies().size());
			assertEquals(1, replySetHelper.getReplies().size());
			System.out.println("");
			for (ReplyHelper rep : replySetHelper.getReplies()) {
				System.out.println(rep.getTitle());
				System.out.println(rep.getAbstract());
			}

			System.out
					.print("getFacets = " + replySetHelper.getFacets().size());
			System.out.println("");
			for (FacetHelper facet : replySetHelper.getFacets()) {

				System.out.println(facet.getLabel() + " ("
						+ facet.getValues().size() + ")");
				System.out.println("id = " + facet.getId());
				if(facet.getId().equalsIgnoreCase("domain")){
					assertEquals("Expected 1 facets values for domain facet", 1, facet.getValues().size());
				}
				for (FacetValueHelperInterface facetValue : facet.getValues()) {
					System.out.println("value : " + facetValue.getLabel()
							+ " (" + facetValue.getCount() + ")");

				}
			}

		}

		// System.out.println(helper.toString());
		/** [Replies helper] */

		/** [View] */
		// return ok(meta.render(helper));
		/** [View] */

	}

}
