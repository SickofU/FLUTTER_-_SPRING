package com.example.demo.repository;

import com.example.demo.entity.Album;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {


}
