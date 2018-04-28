package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Сообщение для обновления ветки обсуждения на форуме. Пустые параметры остаются без изменений. 
 */
@ApiModel(description = "Сообщение для обновления ветки обсуждения на форуме. Пустые параметры остаются без изменений. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class ThreadUpdate   {
  @JsonProperty("title")
  private String title = null;

  @JsonProperty("message")
  private String message = null;

  public ThreadUpdate title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Заголовок ветки обсуждения.
   * @return title
  **/
  @ApiModelProperty(example = "Davy Jones cache", value = "Заголовок ветки обсуждения.")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ThreadUpdate message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Описание ветки обсуждения.
   * @return message
  **/
  @ApiModelProperty(example = "An urgent need to reveal the hiding place of Davy Jones. Who is willing to help in this matter?", value = "Описание ветки обсуждения.")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ThreadUpdate threadUpdate = (ThreadUpdate) o;
    return Objects.equals(this.title, threadUpdate.title) &&
        Objects.equals(this.message, threadUpdate.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ThreadUpdate {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

