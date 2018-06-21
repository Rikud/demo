package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
/**
 * Информация о форуме. 
 */
@ApiModel(description = "Информация о форуме. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Forum   {

  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("title")
  private String title = null;

  @JsonProperty("user")
  private String user = null;

  @JsonProperty("slug")
  private String slug = null;

  @JsonProperty("posts")
  private Integer posts = null;

  @JsonProperty("threads")
  private Integer threads = null;

  public Forum title(String title) {
    this.title = title;
    return this;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

   /**
   * Название форума.
   * @return title
  **/
  @ApiModelProperty(example = "Pirate stories", required = true, value = "Название форума.")
  @NotNull
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Forum user(String user) {
    this.user = user;
    return this;
  }

   /**
   * Nickname пользователя, который отвечает за форум.
   * @return user
  **/
  @ApiModelProperty(example = "j.sparrow", required = true, value = "Nickname пользователя, который отвечает за форум.")
  @NotNull
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Forum slug(String slug) {
    this.slug = slug;
    return this;
  }

   /**
   * Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL), уникальное поле.
   * @return slug
  **/
  @ApiModelProperty(example = "pirate-stories", required = true, value = "Человекопонятный URL (https://ru.wikipedia.org/wiki/%D0%A1%D0%B5%D0%BC%D0%B0%D0%BD%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9_URL), уникальное поле.")
  @NotNull
  @Pattern(regexp="^(\\d|\\w|-|_)*(\\w|-|_)(\\d|\\w|-|_)*$")
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public Forum posts(Integer posts) {
    this.posts = posts;
    return this;
  }

   /**
   * Общее кол-во сообщений в данном форуме. 
   * @return posts
  **/
  @ApiModelProperty(example = "200000.0", readOnly = true, value = "Общее кол-во сообщений в данном форуме. ")
  public Integer getPosts() {
    return posts;
  }

  public void setPosts(Integer posts) {
    this.posts = posts;
  }

  public Forum threads(Integer threads) {
    this.threads = threads;
    return this;
  }

   /**
   * Общее кол-во ветвей обсуждения в данном форуме. 
   * @return threads
  **/
  @ApiModelProperty(example = "200.0", readOnly = true, value = "Общее кол-во ветвей обсуждения в данном форуме. ")
  public Integer getThreads() {
    return threads;
  }

  public void setThreads(Integer threads) {
    this.threads = threads;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Forum forum = (Forum) o;
    return Objects.equals(this.title, forum.title) &&
        Objects.equals(this.user, forum.user) &&
        Objects.equals(this.slug, forum.slug) &&
        Objects.equals(this.posts, forum.posts) &&
        Objects.equals(this.threads, forum.threads);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, user, slug, posts, threads);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Forum {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    posts: ").append(toIndentedString(posts)).append("\n");
    sb.append("    threads: ").append(toIndentedString(threads)).append("\n");
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

