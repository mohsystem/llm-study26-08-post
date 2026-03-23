package com.um.springbootprojstructure.dto;

import java.util.List;

public class LdapUserSearchResult {

    private String status;
    private List<LdapUserSearchResponse> results;

    public LdapUserSearchResult() {}

    public LdapUserSearchResult(String status, List<LdapUserSearchResponse> results) {
        this.status = status;
        this.results = results;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<LdapUserSearchResponse> getResults() { return results; }
    public void setResults(List<LdapUserSearchResponse> results) { this.results = results; }
}
