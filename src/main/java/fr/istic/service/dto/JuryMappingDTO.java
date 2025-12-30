package fr.istic.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public class JuryMappingDTO implements Serializable {

    public String anonymousNumber;
    public String ine;
}
