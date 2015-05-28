package com.micro.arch

import groovy.util.logging.Log
import org.springframework.stereotype.Component

import java.util.logging.Level

@Log
@Component
class BrainFuckService {
    def program = '', memory = [:]
    def instructionPointer = 0, dataPointer = 0

    def execute(program) {
        log.info("BF Program start")
        def response = ''
        try {
            this.program = program
            while (instructionPointer < program.size()) {
                log.fine("Iterating [$instructionPointer]")
                switch (program[instructionPointer++]) {
                    case '>': dataPointer++; break;
                    case '<': dataPointer--; break;
                    case '+': memory[dataPointer] = memoryValue + 1; break;
                    case '-': memory[dataPointer] = memoryValue - 1; break;
                    case ',': memory[dataPointer] = System.in.read(); break;
                    case '.': response += ((char) memoryValue); break;
                    case '[': handleLoopStart(); break;
                    case ']': handleLoopEnd(); break;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, 'Problem running.', e)
            response = "Impossible to run program: [$program]"
        }
        log.info("Done [$response]")
        return response
    }

    private getMemoryValue() { memory[dataPointer] ?: 0 }

    private handleLoopStart() {
        if (memoryValue) return

        int depth = 1;
        while (instructionPointer < program.size()) {
            switch (program[instructionPointer++]) {
                case '[': depth++; break;
                case ']': if (!(--depth)) return;
            }
        }
        throw new IllegalStateException('Could not find matching end bracket')
    }

    private handleLoopEnd() {
        int depth = 0
        while (instructionPointer >= 0) {
            switch (program[--instructionPointer]) {
                case ']': depth++; break;
                case '[': if (!(--depth)) return; break;
            }
        }
        throw new IllegalStateException('Could not find matching start bracket')
    }
}