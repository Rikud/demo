package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Запись в списке задач. 
 */
@ApiModel(description = "Запись в списке задач. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-20T11:21:44.341-07:00")

public class Item   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("completed")
  private Boolean completed = null;

  public Item id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор записи.
   * @return id
  **/
  @ApiModelProperty(value = "Идентификатор записи.")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Item description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Описание задачи.
   * @return description
  **/
  @ApiModelProperty(value = "Описание задачи.")
  @Size(min=1)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Item completed(Boolean completed) {
    this.completed = completed;
    return this;
  }

   /**
   * Флаг выполненности.
   * @return completed
  **/
  @ApiModelProperty(value = "Флаг выполненности.")
  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Item item = (Item) o;
    return Objects.equals(this.id, item.id) &&
        Objects.equals(this.description, item.description) &&
        Objects.equals(this.completed, item.completed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, completed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Item {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    completed: ").append(toIndentedString(completed)).append("\n");
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

