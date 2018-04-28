package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
/**
 * Информация о пользователе. 
 */
@ApiModel(description = "Информация о пользователе. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class User   {

  @JsonProperty("id")
  private BigDecimal id = null;

  @JsonProperty("nickname")
  private String nickname = null;

  @JsonProperty("fullname")
  private String fullname = null;

  @JsonProperty("about")
  private String about = null;

  @JsonProperty("email")
  private String email = null;

  public User nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  @ApiModelProperty(example = "42.0", readOnly = true, value = "Идентификатор ветки обсуждения.")
  public BigDecimal getId() {
    return id;
  }

  public void setId(BigDecimal id) {
    this.id = id;
  }

  /**
   * Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания. Сравнение имени регистронезависимо. 
   * @return nickname
  **/
  @ApiModelProperty(example = "j.sparrow", readOnly = true, value = "Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания. Сравнение имени регистронезависимо. ")
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public User fullname(String fullname) {
    this.fullname = fullname;
    return this;
  }

   /**
   * Полное имя пользователя.
   * @return fullname
  **/
  @ApiModelProperty(example = "Captain Jack Sparrow", required = true, value = "Полное имя пользователя.")
  @NotNull
  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public User about(String about) {
    this.about = about;
    return this;
  }

   /**
   * Описание пользователя.
   * @return about
  **/
  @ApiModelProperty(example = "This is the day you will always remember as the day that you almost caught Captain Jack Sparrow!", value = "Описание пользователя.")
  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public User email(String email) {
    this.email = email;
    return this;
  }

   /**
   * Почтовый адрес пользователя (уникальное поле).
   * @return email
  **/
  @ApiModelProperty(example = "captaina@blackpearl.sea", required = true, value = "Почтовый адрес пользователя (уникальное поле).")
  @NotNull
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.nickname, user.nickname) &&
        Objects.equals(this.fullname, user.fullname) &&
        Objects.equals(this.about, user.about) &&
        Objects.equals(this.email, user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nickname, fullname, about, email);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    fullname: ").append(toIndentedString(fullname)).append("\n");
    sb.append("    about: ").append(toIndentedString(about)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

  public User setProfile(UserUpdate profile) {
    if (profile != null) {
      if (profile.getEmail() != null && profile.getEmail() != "")
        this.setEmail(profile.getEmail());
      if (profile.getAbout() != null && profile.getAbout() != "")
        this.setAbout(profile.getAbout());
      if (profile.getFullname() != null && profile.getFullname() != "")
        this.setFullname(profile.getFullname());
    }
    return this;
  }
}

