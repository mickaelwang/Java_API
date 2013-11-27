package net.antidot.api.search;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.antidot.api.common.ApiInternalError;

import org.apache.http.client.utils.URIBuilder;

/** Encodes/decodes queries.
 * <p>
 * Creates query from current parameters or generates appropriate links.
 * Instance of this class should be used when generating result pages.
 */
public class QueryCoder {

	private String uri;
	private CoderManager coderMgr;

	/** Constructs new query encoder.
	 * @param uri [in] URI base path.
	 * @param coderMgr [in] coder manager (feed coder, filter coder...).
	 * 		  It is used for link generation.
	 */
	public QueryCoder(String uri, CoderManager coderMgr) {
		this.uri = uri;
		this.coderMgr = coderMgr;
	}
	
	/** Creates new query from parameters.
	 * <p>
	 * This method should be called with parameters coming from URL parameters.
	 * @param parameters [in] parameters used to initialize the query.
	 * @param coderMgr [in] manager for various coders (feed, filter...). 
	 * @return newly created query.
	 */
	public Query decode(Map<String, String[]> parameters) {
		Query result = Query.create();
		Integer page = null; // Page should be set last otherwise it is reset to 1
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue(); // should always contain one value
			for (String value : values) {
				if (key.equals(Query.FILTER)) {
					for (Entry<String, List<String>> decoded : coderMgr.getFilterCoder().decode(value).entrySet()) {
						for (String filterValue : decoded.getValue()) {
							result = result.addFilter(decoded.getKey(), filterValue);
						}
					}
				} else if (key.equals(Query.QUERY)) {
					result = result.setSearchString(value);
				} else if (key.equals(Query.PAGE)) {
					page = Integer.parseInt(value);
				} else if (key.equals(Query.REPLIES)) {
					result = result.setReplies(Integer.parseInt(value));
				} else if (key.equals(Query.FEED)) {
					for (String decoded : coderMgr.getFeedCoder().decode(value)) {
						result = result.addFeed(decoded);
					}
				} else if (key.equals(Query.LANG)) {
					result = result.setLanguage(value);
				} else if (key.equals(Query.SORT)) {
					result = result.setSort(value);
				} else {
					Logger.getLogger("search").warning("Ignoring unknown parameter: " + key);
				}
			}
		}
		if (page != null) {
			result = result.setPage(page);
		}
		return result;
	}
	
	/** Generates links for the given query.
	 * @param query [in] query to encode in order to generate corresponding link.
	 * @return URI corresponding to provided query. 
	 * @throws URISyntaxException cannot generate link.
	 * 		   It should only happen when the query encoder has been initialized with bad URI.
	 */
	public String generateLink(Query query) throws URISyntaxException {
		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(uri);
		} catch (URISyntaxException e) { // Should never happen
			throw new ApiInternalError("Cannot buil URI", e);
		}

		if (query.hasFeed()) {
			uriBuilder.addParameter(Query.FEED, coderMgr.getFeedCoder().encode(query.getFeeds()));
		}
		if (query.hasSearchString()) {
			uriBuilder.addParameter(Query.QUERY, query.getSearchString());
		}
		if (query.hasPage()) {
			uriBuilder.addParameter(Query.PAGE, String.valueOf(query.getPage()));
		}
		if (query.hasReplies()) {
			uriBuilder.addParameter(Query.REPLIES, String.valueOf(query.getReplies()));
		}
		if (query.hasLanguage()) {
			uriBuilder.addParameter(Query.LANG, query.getLanguage());
		}
		if (query.hasSort()) {
			uriBuilder.addParameter(Query.SORT, query.getSort());
		}
		if (query.hasFilter()) {
			uriBuilder.addParameter(Query.FILTER, coderMgr.getFilterCoder().encode(query.getFilters()));
		}

		return uriBuilder.build().toString();
	}
}
