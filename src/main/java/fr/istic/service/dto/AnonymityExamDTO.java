package fr.istic.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * A DTO for the {@link fr.istic.domain.AnonymityExam} entity.
 */
@RegisterForReflection
public class AnonymityExamDTO implements Serializable {

    public Long id;

    @NotNull
    public String anonymousNumber;

    @NotNull
    public Long examId;

    public Long sheetId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnonymityExamDTO)) {
            return false;
        }
        return id != null && id.equals(((AnonymityExamDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AnonymityExamDTO{" +
            "id=" + id +
            ", anonymousNumber='" + anonymousNumber + "'" +
            ", examId=" + examId +
            ", sheetId=" + sheetId +
            "}";
    }
}
