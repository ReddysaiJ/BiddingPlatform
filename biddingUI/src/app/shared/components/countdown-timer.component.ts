import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-countdown-timer',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div
            class="timer"
            [ngClass]="{
                'timer-end': mode === 'end',
                'timer-start': mode === 'start',
            }"
        >
            {{ timeLeft }}
        </div>
    `,
    styles: [`
        .timer {
            font-size: 16px;
            font-weight: 700;
        }

        .timer-end {
            color: #f59e0b;
        }

        .timer-start {
            color: #22c55e;
        }
    `],
})
export class CountdownTimerComponent implements OnInit, OnDestroy {
    @Input() endTime!: string;

    @Input() mode: 'start' | 'end' = 'end';

    timeLeft = '';
    interval: any;

    constructor(private cdr: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.updateTimer();

        this.interval = setInterval(() => {
            this.updateTimer();
            this.cdr.markForCheck();
        }, 1000);
    }

    updateTimer() {
        const target = new Date(this.endTime).getTime();
        const now = new Date().getTime();

        const diff = target - now;

        if (diff <= 0) {
            this.timeLeft = this.mode === 'start' ? 'Auction Started' : 'Auction Ended';

            clearInterval(this.interval);
            return;
        }

        const d = Math.floor(diff / (1000 * 60 * 60 * 24));
        const h = Math.floor((diff / (1000 * 60 * 60)) % 24);
        const m = Math.floor((diff / (1000 * 60)) % 60);
        const s = Math.floor((diff / 1000) % 60);

        this.timeLeft = `${d}d : ${h}h : ${m}m : ${s}s`;
    }

    ngOnDestroy(): void {
        clearInterval(this.interval);
    }
}
