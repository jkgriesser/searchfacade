package io.tradeledger.searchfacade.model;

import org.springframework.data.annotation.Id;

public final class Language {

    @Id
    private final String id;

    private final String language;
    private final Integer version;
    private final Boolean isJvmBased;

    public Language(String id, String language, Integer version, Boolean isJvmBased) {
        this.id = id;
        this.language = language;
        this.version = version;
        this.isJvmBased = isJvmBased;
    }

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getVersion() {
        return version;
    }

    public Boolean getJvmBased() {
        return isJvmBased;
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
