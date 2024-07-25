package com.jawnpaul.shopapp.utils

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection

class ShopAppRequestDispatcher {

    /**
     * Return ok response from mock server
     */
    inner class RequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                PRODUCT_BUNDLE_PATH -> {
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(getJson("response/product_list.json"))
                }

                else -> throw IllegalArgumentException("Unknown Request Path ${request.path}")
            }
        }
    }

    /**
     * Return bad request response from mock server
     */
    internal inner class BadRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) =
            MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
    }

    /**
     * Return server error response from mock server
     */
    internal inner class ErrorRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) =
            MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
    }

    /**
     * Return incorrect body(aka null) response from mock server
     */
    internal inner class IncorrectBodyRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) =
            MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT)
    }
}
