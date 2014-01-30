package com.ariadne.domain

class TestPerson {

    static auditable = [ignore: ['surName'], include: ['name']]

    String name
    String surName

    static constraints = {
    }
}
