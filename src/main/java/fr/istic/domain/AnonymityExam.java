package fr.istic.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbTransient;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * An AnonymityExam.
 */
@Entity
@Table(name = "anonymity_exam")
@Cacheable
@RegisterForReflection
public class AnonymityExam extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "anonymous_number", nullable = false)
    public String anonymousNumber;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonbTransient
    public Exam exam;

    @ManyToOne
    @JoinColumn(name = "sheet_id")
    @JsonbTransient
    public ExamSheet sheet;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnonymityExam)) {
            return false;
        }
        return id != null && id.equals(((AnonymityExam) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AnonymityExam{" +
            "id=" + id +
            ", anonymousNumber='" + anonymousNumber + "'" +
            "}";
    }

    public AnonymityExam update() {
        return update(this);
    }

    public AnonymityExam persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static AnonymityExam update(AnonymityExam anonymityExam) {
        if (anonymityExam == null) {
            throw new IllegalArgumentException("anonymityExam can't be null");
        }
        var entity = AnonymityExam.<AnonymityExam>findById(anonymityExam.id);
        if (entity != null) {
            entity.anonymousNumber = anonymityExam.anonymousNumber;
            entity.exam = anonymityExam.exam;
            entity.sheet = anonymityExam.sheet;
        }
        return entity;
    }

    public static AnonymityExam persistOrUpdate(AnonymityExam anonymityExam) {
        if (anonymityExam == null) {
            throw new IllegalArgumentException("anonymityExam can't be null");
        }
        if (anonymityExam.id == null) {
            persist(anonymityExam);
            return anonymityExam;
        } else {
            return update(anonymityExam);
        }
    }

    @Transactional
    public static AnonymityExam assignExam(long examId, String anonymousNumber) {
        if (anonymousNumber == null || anonymousNumber.isBlank()) {
            throw new IllegalArgumentException("anonymousNumber can't be null/blank");
        }

        Exam exam = Exam.findById(examId);
        if (exam == null) {
            throw new IllegalArgumentException("exam not found: " + examId);
        }

        AnonymityExam entity = findByExamIdAndNumber(examId, anonymousNumber).firstResult();
        if (entity == null) {
            entity = new AnonymityExam();
            entity.anonymousNumber = anonymousNumber;
            entity.exam = exam;
            entity.persist();
        }
        return entity;
    }

    @Transactional
    public static long assignSheet(long examId, String anonymousNumber, long sheetId) {
        if (anonymousNumber == null || anonymousNumber.isBlank()) {
            throw new IllegalArgumentException("anonymousNumber can't be null/blank");
        }

        AnonymityExam entity = findByExamIdAndNumber(examId, anonymousNumber).firstResult();
        if (entity == null) {
            throw new IllegalArgumentException("anonymousNumber not found for exam: " + anonymousNumber);
        }

        ExamSheet sheet = ExamSheet.findById(sheetId);
        if (sheet == null) {
            throw new IllegalArgumentException("sheet not found: " + sheetId);
        }

        AnonymityExam already = findByExamIdAndSheetId(examId, sheetId).firstResult();
        if (already != null && (entity.id == null || !already.id.equals(entity.id))) {
            throw new IllegalArgumentException("sheet already assigned to another anonymousNumber");
        }

        return update("sheet = ?1 where id = ?2", sheet, entity.id);
    }

    @Transactional
    public static long unassignSheet(long examId, String anonymousNumber) {
        return update("sheet = null where exam.id = ?1 and anonymousNumber = ?2", examId, anonymousNumber);
    }

    public static PanacheQuery<AnonymityExam> findByExamId(long examId) {
        return find("select anonymity from AnonymityExam anonymity where anonymity.exam.id =?1", examId);
    }

    public static PanacheQuery<AnonymityExam> findByExamIdAndNumber(long examId, String anonymousNumber) {
        return find("select anonymity from AnonymityExam anonymity where anonymity.exam.id =?1 and anonymity.anonymousNumber =?2", examId, anonymousNumber);
    }

    public static PanacheQuery<AnonymityExam> findByExamIdAndSheetId(long examId, long sheetId) {
        return find("select anonymity from AnonymityExam anonymity where anonymity.exam.id =?1 and anonymity.sheet.id =?2", examId, sheetId);
    }

    public static PanacheQuery<AnonymityExam> findUnassignedByExamId(long examId) {
        return find("select anonymity from AnonymityExam anonymity where anonymity.exam.id =?1 and anonymity.sheet is null", examId);
    }

    public static PanacheQuery<AnonymityExam> findAssignedByExamId(long examId) {
        return find("select ae from AnonymityExam ae where ae.exam.id = ?1 and ae.sheet is not null", examId);
    }

    public static PanacheQuery<FinalResult> findFinalResultsWithAnonByExamId(long examId) {
        return find("select fr from FinalResult fr where fr.exam.id = ?1 and fr.anonymityExam is not null", examId);
    }
}
