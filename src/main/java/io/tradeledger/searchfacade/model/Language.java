package io.tradeledger.searchfacade.model;

import org.springframework.data.annotation.Id;

public class Language {

    @Id
    private String id;

    private String language;
    private Integer version;
    private Boolean isJvmBased;

    public Language(String id, String language, Integer version, Boolean isJvmBased) {
        this.id = id;
        this.language = language;
        this.version = version;
        this.isJvmBased = isJvmBased;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getJvmBased() {
        return isJvmBased;
    }

    public void setJvmBased(Boolean jvmBased) {
        isJvmBased = jvmBased;
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", language='" + language + '\'' +
                ", version=" + version +
                ", isJvmBased=" + isJvmBased +
                '}';
    }

}
