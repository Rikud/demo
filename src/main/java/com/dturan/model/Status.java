package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.validation.constraints.*;
/**
 * Status
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Status   {
  @JsonProperty("user")
  private BigDecimal user = null;

  @JsonProperty("forum")
  private BigDecimal forum = null;

  @JsonProperty("thread")
  private BigDecimal thread = null;

  @JsonProperty("post")
  private BigDecimal post = null;

  public Status user(BigDecimal user) {
    this.user = user;
    return this;
  }

   /**
   * Кол-во пользователей в базе данных.
   * @return user
  **/
  @ApiModelProperty(example = "1000.0", required = true, value = "Кол-во пользователей в базе данных.")
  @NotNull
  public BigDecimal getUser() {
    return user;
  }

  public void setUser(BigDecimal user) {
    this.user = user;
  }

  public Status forum(BigDecimal forum) {
    this.forum = forum;
    return this;
  }

   /**
   * Кол-во разделов в базе данных.
   * @return forum
  **/
  @ApiModelProperty(example = "100.0", required = true, value = "Кол-во разделов в базе данных.")
  @NotNull
  public BigDecimal getForum() {
    return forum;
  }

  public void setForum(BigDecimal forum) {
    this.forum = forum;
  }

  public Status thread(BigDecimal thread) {
    this.thread = thread;
    return this;
  }

   /**
   * Кол-во веток обсуждения в базе данных.
   * @return thread
  **/
  @ApiModelProperty(example = "1000.0", required = true, value = "Кол-во веток обсуждения в базе данных.")
  @NotNull
  public BigDecimal getThread() {
    return thread;
  }

  public void setThread(BigDecimal thread) {
    this.thread = thread;
  }

  public Status post(BigDecimal post) {
    this.post = post;
    return this;
  }

   /**
   * Кол-во сообщений в базе данных.
   * @return post
  **/
  @ApiModelProperty(example = "1000000.0", required = true, value = "Кол-во сообщений в базе данных.")
  @NotNull
  public BigDecimal getPost() {
    return post;
  }

  public void setPost(BigDecimal post) {
    this.post = post;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Status status = (Status) o;
    return Objects.equals(this.user, status.user) &&
        Objects.equals(this.forum, status.forum) &&
        Objects.equals(this.thread, status.thread) &&
        Objects.equals(this.post, status.post);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, forum, thread, post);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Status {\n");
    
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    forum: ").append(toIndentedString(forum)).append("\n");
    sb.append("    thread: ").append(toIndentedString(thread)).append("\n");
    sb.append("    post: ").append(toIndentedString(post)).append("\n");
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

