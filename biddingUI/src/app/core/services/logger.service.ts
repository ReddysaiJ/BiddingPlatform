import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

export enum LogLevel {
    DEBUG = 0,
    INFO = 1,
    WARN = 2,
    ERROR = 3
}

@Injectable({ providedIn: 'root' })
export class LoggerService {
    private logLevel: LogLevel;

    constructor() {
        this.logLevel = this.getLogLevelFromEnvironment();
    }

    debug(message: string, data?: any): void {
        if (this.logLevel <= LogLevel.DEBUG) {
            console.debug(`[DEBUG] ${message}`, data);
        }
    }

    info(message: string, data?: any): void {
        if (this.logLevel <= LogLevel.INFO) {
            console.log(`[INFO] ${message}`, data);
        }
    }

    warn(message: string, data?: any): void {
        if (this.logLevel <= LogLevel.WARN) {
            console.warn(`[WARN] ${message}`, data);
        }
    }

    error(message: string, data?: any): void {
        if (this.logLevel <= LogLevel.ERROR) {
            console.error(`[ERROR] ${message}`, data);
        }
    }

    private getLogLevelFromEnvironment(): LogLevel {
        if (!environment.logLevel) {
            return LogLevel.INFO;
        }

        const levelStr = environment.logLevel.toUpperCase();
        const level = LogLevel[levelStr as keyof typeof LogLevel];
        return level !== undefined ? level : LogLevel.INFO;
    }
}
