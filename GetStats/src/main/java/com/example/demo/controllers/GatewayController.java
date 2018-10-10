package com.example.demo.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/gwcontroller/heartbeat")
@RestController
public class GatewayController {

	/**
	 * this API should be called by GW DIagnostics to save the heartbeat which
	 * will eventually call the Node.js server which will persist in Mongo
	 * 
	 * @param heartbeat
	 * @return
	 */
	@POST
	@RequestMapping("/post")
	@Consumes("application/json")
	public ResponseEntity<String> createHearbeatInJSON(Heartbeat heartbeat) {

		// Call actual Node.js service rest API

		// String result = "Hearbeat  : " + heartbeat;
		String result = callServiceToPersistHearbeat(heartbeat);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);

	}

	@GET
	@RequestMapping("/get")
	@Produces("application/json")
	public ResponseEntity<String> getHeartbeats() {

		// Call actual Node.js service rest API

		String res = callServiceToGetHearbeats();
		// JSONArray ibj = null;
		// try {
		// ibj = new JSONArray(res);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return ResponseEntity.ok().body(res);

	}

	/**
	 * actually calls Node.js code and calls its REST API to persist the
	 * Hearbeat
	 * 
	 * @param hearbeat
	 */

	private String callServiceToGetHearbeats() {

		StringBuilder builder = new StringBuilder();

		try {

			URL url = new URL("http://localhost:3000/heartbeats");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Hearbeats Node JS Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				builder.append(output);

			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return builder.toString();

	}

	private static String callServiceToPersistHearbeat(Heartbeat hearbeat) {
		StringBuilder builder = new StringBuilder();

		try {

			JSONObject object = new JSONObject();
			object.put("gw_uuid", hearbeat.getGw_uuid());
			object.put("timestamp", hearbeat.getTimestamp());

			object.put("status", hearbeat.getStatus());

			System.out.println(object.toString());

			URL url = new URL("http://localhost:3000/heartbeats");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"gw_uuid\":\"sharsad\",\"timestamp\":83838838, \"status\":\"ok\"}";

			OutputStream os = conn.getOutputStream();
			os.write(object.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Hearbeats Node JS Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				builder.append(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static void main(String args[]) throws InterruptedException,
			ExecutionException {

		Heartbeat beat = new Heartbeat();
		beat.setGw_uuid("sharad121");
		beat.setStatus("ok");
		beat.setTimestamp(828282828);
		// callServiceToGetHearbeats();
		callServiceToPersistHearbeat(beat);

	}

}
