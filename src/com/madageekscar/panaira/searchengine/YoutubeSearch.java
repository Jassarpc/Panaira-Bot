/**
 * Original code sample here https://developers.google.com/youtube/v3/code_samples/java#search_by_keyword
 */

package com.madageekscar.panaira.searchengine;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.madageekscar.panaira.results.YoutubeResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class YoutubeSearch {

    private static final String API_KEY = "AIzaSyCd9Rq846BRJFZXErPkJngBLFBTiHCwu18";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    public static List<YoutubeResult> search(String searchTerm) {

        List<YoutubeResult> youtubeSearchResults = new ArrayList<>();
        YoutubeResult youtubeResult;

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            search.setKey(API_KEY);
            search.setQ(searchTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/high/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {

                Iterator<SearchResult> iterator = searchResultList.iterator();


                if (!iterator.hasNext()) {
                    System.out.println(" There aren't any results for your query.");
                }

                while (iterator.hasNext()) {

                    SearchResult singleVideo = iterator.next();
                    ResourceId rId = singleVideo.getId();

                    // Confirm that the result represents a video. Otherwise, the
                    // item will not contain a video ID.
                    if (rId.getKind().equals("youtube#video")) {

                        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();

                        youtubeResult = new YoutubeResult(rId.getVideoId(),
                                singleVideo.getSnippet().getTitle(),
                                thumbnail.getUrl(),
                                singleVideo.getSnippet().getDescription());

                        youtubeSearchResults.add(youtubeResult);

                    }



                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return youtubeSearchResults;

    }


}

