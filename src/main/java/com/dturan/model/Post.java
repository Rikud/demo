package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Objects;
/**
 * Сообщение внутри ветки обсуждения на форуме. 
 */
@ApiModel(description = "Сообщение внутри ветки обсуждения на форуме. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Post   {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("parent")
  private Integer parent = null;

  @JsonProperty("author")
  private String author = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("isEdited")
  private Boolean isEdited = null;

  @JsonProperty("forum")
  private String forum = null;

  @JsonProperty("thread")
  private Integer thread = null;

  @JsonProperty("created")
  private String created = null;

  public Post id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор данного сообщения.
   * @return id
  **/
  @ApiModelProperty(readOnly = true, value = "Идентификатор данного сообщения.")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Post parent(Integer parent) {
    this.parent = parent;
    return this;
  }

   /**
   * Идентификатор родительского сообщения (0 - корневое сообщение обсуждения). 
   * @return parent
  **/
  @ApiModelProperty(value = "Идентификатор родительского сообщения (0 - корневое сообщение обсуждения). ")
  public Integer getParent() {
    return parent;
  }

  public void setParent(Integer parent) {
    this.parent = parent;
  }

  public Post author(String author) {
    this.author = author;
    return this;
  }

   /**
   * Автор, написавший данное сообщение.
   * @return author
  **/
  @ApiModelProperty(example = "j.sparrow", required = true, value = "Автор, написавший данное сообщение.")
  @NotNull
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Post message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Собственно сообщение форума.
   * @return message
  **/
  @ApiModelProperty(example = "We should be afraid of the Kraken.", required = true, value = "Собственно сообщение форума.")
  @NotNull
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Post isEdited(Boolean isEdited) {
    this.isEdited = isEdited;
    return this;
  }

   /**
   * Истина, если данное сообщение было изменено.
   * @return isEdited
  **/
  @ApiModelProperty(readOnly = true, value = "Истина, если данное сообщение было изменено.")
  public Boolean getIsEdited() {
    return isEdited;
  }

  public void setIsEdited(Boolean isEdited) {
    this.isEdited = isEdited;
  }

  public Post forum(String forum) {
    this.forum = forum;
    return this;
  }

   /**
   * Идентификатор форума (slug) данного сообещния.
   * @return forum
  **/
  @ApiModelProperty(readOnly = true, value = "Идентификатор форума (slug) данного сообещния.")
  public String getForum() {
    return forum;
  }

  public void setForum(String forum) {
    this.forum = forum;
  }

  public Post thread(Integer thread) {
    this.thread = thread;
    return this;
  }

   /**
   * Идентификатор ветви (id) обсуждения данного сообещния.
   * @return thread
  **/
  @ApiModelProperty(readOnly = true, value = "Идентификатор ветви (id) обсуждения данного сообещния.")
  public Integer getThread() {
    return thread;
  }

  public void setThread(Integer thread) {
    this.thread = thread;
  }

  public Post created(DateTime created) {
    this.created = created.toString();
    return this;
  }

   /**
   * Дата создания сообщения на форуме.
   * @return created
  **/
  @ApiModelProperty(readOnly = true, value = "Дата создания сообщения на форуме.")
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
    Post post = (Post) o;
    return Objects.equals(this.id, post.id) &&
        Objects.equals(this.parent, post.parent) &&
        Objects.equals(this.author, post.author) &&
        Objects.equals(this.message, post.message) &&
        Objects.equals(this.isEdited, post.isEdited) &&
        Objects.equals(this.forum, post.forum) &&
        Objects.equals(this.thread, post.thread) &&
        Objects.equals(this.created, post.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parent, author, message, isEdited, forum, thread, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Post {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    isEdited: ").append(toIndentedString(isEdited)).append("\n");
    sb.append("    forum: ").append(toIndentedString(forum)).append("\n");
    sb.append("    thread: ").append(toIndentedString(thread)).append("\n");
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
}

