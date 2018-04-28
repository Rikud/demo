package com.dturan.model;

import java.util.Objects;
import com.dturan.model.Forum;
import com.dturan.model.Post;
import com.dturan.model.Thread;
import com.dturan.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Полная информация о сообщении, включая связанные объекты. 
 */
@ApiModel(description = "Полная информация о сообщении, включая связанные объекты. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class PostFull   {
  @JsonProperty("post")
  private Post post = null;

  @JsonProperty("author")
  private User author = null;

  @JsonProperty("thread")
  private Thread thread = null;

  @JsonProperty("forum")
  private Forum forum = null;

  public PostFull post(Post post) {
    this.post = post;
    return this;
  }

   /**
   * Get post
   * @return post
  **/
  @ApiModelProperty(value = "")
  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public PostFull author(User author) {
    this.author = author;
    return this;
  }

   /**
   * Get author
   * @return author
  **/
  @ApiModelProperty(value = "")
  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public PostFull thread(Thread thread) {
    this.thread = thread;
    return this;
  }

   /**
   * Get thread
   * @return thread
  **/
  @ApiModelProperty(value = "")
  public Thread getThread() {
    return thread;
  }

  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public PostFull forum(Forum forum) {
    this.forum = forum;
    return this;
  }

   /**
   * Get forum
   * @return forum
  **/
  @ApiModelProperty(value = "")
  public Forum getForum() {
    return forum;
  }

  public void setForum(Forum forum) {
    this.forum = forum;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostFull postFull = (PostFull) o;
    return Objects.equals(this.post, postFull.post) &&
        Objects.equals(this.author, postFull.author) &&
        Objects.equals(this.thread, postFull.thread) &&
        Objects.equals(this.forum, postFull.forum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(post, author, thread, forum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostFull {\n");
    
    sb.append("    post: ").append(toIndentedString(post)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    thread: ").append(toIndentedString(thread)).append("\n");
    sb.append("    forum: ").append(toIndentedString(forum)).append("\n");
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

