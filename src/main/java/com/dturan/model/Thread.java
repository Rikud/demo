package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
/**
 * Ветка обсуждения на форуме. 
 */
@ApiModel(description = "Ветка обсуждения на форуме. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Thread   {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("title")
  private String title = null;

  @JsonProperty("author")
  private String author = null;

  @JsonProperty("forum")
  private String forum = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("votes")
  private Integer votes = null;

  @JsonProperty("slug")
  private String slug = null;

  @JsonProperty("created")
  private String created = null;

  public Thread id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор ветки обсуждения.
   * @return id
  **/
  @ApiModelProperty(example = "42.0", readOnly = true, value = "Идентификатор ветки обсуждения.")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Thread title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Заголовок ветки обсуждения.
   * @return title
  **/
  @ApiModelProperty(example = "Davy Jones cache", required = true, value = "Заголовок ветки обсуждения.")
  @NotNull
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Thread author(String author) {
    this.author = author;
    return this;
  }

   /**
   * Пользователь, создавший данную тему.
   * @return author
  **/
  @ApiModelProperty(example = "j.sparrow", required = true, value = "Пользователь, создавший данную тему.")
  @NotNull
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Thread forum(String forum) {
    this.forum = forum;
    return this;
  }

   /**
   * Форум, в котором расположена данная ветка обсуждения.
   * @return forum
  **/
  @ApiModelProperty(example = "pirate-stories", readOnly = true, value = "Форум, в котором расположена данная ветка обсуждения.")
  public String getForum() {
    return forum;
  }

  public void setForum(String forum) {
    this.forum = forum;
  }

  public Thread message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Описание ветки обсуждения.
   * @return message
  **/
  @ApiModelProperty(example = "An urgent need to reveal the hiding place of Davy Jones. Who is willing to help in this matter?", required = true, value = "Описание ветки обсуждения.")
  @NotNull
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Thread votes(Integer votes) {
    this.votes = votes;
    return this;
  }

   /**
   * Кол-во голосов непосредственно за данное сообщение форума.
   * @return votes
  **/
  @ApiModelProperty(readOnly = true, value = "Кол-во голосов непосредственно за данное сообщение форума.")
  public Integer getVotes() {
    return votes;
  }

  public void setVotes(Integer votes) {
    this.votes = votes;
  }

  public Thread slug(String slug) {
    this.slug = slug;
    return this;
  }

   /**
   * Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL). В данной структуре slug опционален и не может быть числом.
   * @return slug
  **/
  @ApiModelProperty(example = "jones-cache", readOnly = true, value = "Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL). В данной структуре slug опционален и не может быть числом. ")
  @Pattern(regexp="^(\\d|\\w|-|_)*(\\w|-|_)(\\d|\\w|-|_)*$")
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public Thread created(DateTime created) {
    this.created = created.toString();
    return this;
  }

   /**
   * Дата создания ветки на форуме.
   * @return created
  **/
  @ApiModelProperty(example = "2017-01-01T00:00:00.000+0000", value = "Дата создания ветки на форуме.")
  public String getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created.toString();
  }
  public void setCreated(String created) {
    this.created = created;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Thread thread = (Thread) o;
    return Objects.equals(this.id, thread.id) &&
        Objects.equals(this.title, thread.title) &&
        Objects.equals(this.author, thread.author) &&
        Objects.equals(this.forum, thread.forum) &&
        Objects.equals(this.message, thread.message) &&
        Objects.equals(this.votes, thread.votes) &&
        Objects.equals(this.slug, thread.slug) &&
        Objects.equals(this.created, thread.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, author, forum, message, votes, slug, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Thread {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    forum: ").append(toIndentedString(forum)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    votes: ").append(toIndentedString(votes)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

/*  public void generateSlug() {
    if (this.slug == null && this.title != null) {
      this.slug = this.title.replace(' ', '-');
      this.slug = this.slug.replace('.', ',');
      this.slug = this.slug.replaceAll("/[\\.,\\/#!$%\\^&\\*;:{}=_`~()]/g", "");

      this.slug = this.slug.toLowerCase();
    }
  }*/
}

