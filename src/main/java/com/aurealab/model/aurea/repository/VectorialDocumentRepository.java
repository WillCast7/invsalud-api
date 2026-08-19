package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.VectorialDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorialDocumentRepository extends JpaRepository<VectorialDocumentEntity, Long> {

    List<VectorialDocumentEntity> findByDocumentTitle(String documentTitle);

    List<VectorialDocumentEntity> findByDocumentTitleAndActiveTrue(String documentTitle);

    Page<VectorialDocumentEntity> findByActiveTrue(Pageable pageable);

    Page<VectorialDocumentEntity> findByModuleCodeAndActiveTrue(String moduleCode, Pageable pageable);

    @Query("SELECT v FROM VectorialDocumentEntity v WHERE v.active = true " +
           "AND (:moduleCode IS NULL OR :moduleCode = '' OR v.moduleCode = :moduleCode) " +
           "AND (LOWER(CAST(v.documentTitle AS string)) LIKE CONCAT('%', :search, '%') " +
           "OR LOWER(CAST(v.chunkContent AS string)) LIKE CONCAT('%', :search, '%') " +
           "OR LOWER(CAST(v.metadata AS string)) LIKE CONCAT('%', :search, '%'))")
    Page<VectorialDocumentEntity> searchDocuments(@Param("search") String search,
                                                 @Param("moduleCode") String moduleCode,
                                                 Pageable pageable);

    @Query("SELECT v FROM VectorialDocumentEntity v WHERE v.active = true " +
           "AND (:moduleCode IS NULL OR :moduleCode = '' OR :moduleCode = 'TODOS' OR v.moduleCode = :moduleCode) " +
           "AND (LOWER(CAST(v.documentTitle AS string)) LIKE CONCAT('%', :search, '%') " +
           "OR LOWER(CAST(v.chunkContent AS string)) LIKE CONCAT('%', :search, '%') " +
           "OR LOWER(CAST(v.metadata AS string)) LIKE CONCAT('%', :search, '%'))")
    List<VectorialDocumentEntity> findRelevantChunks(@Param("search") String search,
                                                     @Param("moduleCode") String moduleCode,
                                                     Pageable pageable);

    @Query("SELECT v FROM VectorialDocumentEntity v WHERE v.active = true " +
           "AND (:moduleCode IS NULL OR :moduleCode = '' OR :moduleCode = 'TODOS' OR v.moduleCode = :moduleCode)")
    List<VectorialDocumentEntity> findChunksByModule(@Param("moduleCode") String moduleCode, Pageable pageable);

    @Query("SELECT DISTINCT v.documentTitle FROM VectorialDocumentEntity v WHERE v.active = true")
    List<String> findDistinctDocumentTitles();

    @Modifying
    @Query("DELETE FROM VectorialDocumentEntity v WHERE v.documentTitle = :documentTitle")
    void deleteByDocumentTitle(@Param("documentTitle") String documentTitle);

    @Modifying
    @Query("UPDATE VectorialDocumentEntity v SET v.active = false, v.status = 'REEMPLAZADO' WHERE v.documentTitle = :documentTitle")
    void deactivatePreviousVersions(@Param("documentTitle") String documentTitle);
}
