import { Auction } from './../../core/models/auction.model';
import { ChangeDetectorRef, Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuctionService } from '../../core/services/auction.service';
import { CreateAuctionRequest } from '../../core/models/createAuctionRequest.model';
import { UpdateAuctionRequest } from '../../core/models/updateAuctionRequest.model';

interface CalCell {
    day: number;
    month: number;
    year: number;
    selected: boolean;
    today: boolean;
    otherMonth: boolean;
    disabled: boolean;
}

@Component({
    selector: 'app-auction-form',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './auction-form.component.html',
    styles: [
        `
            .dp-container {
                position: relative;
            }
            .dp-trigger {
                display: flex;
                align-items: center;
                gap: 8px;
                border: 1px solid #ced4da;
                border-radius: 8px;
                padding: 8px 12px;
                cursor: pointer;
                font-size: 14px;
                background: #fff;
                min-height: 38px;
                user-select: none;
                transition: border-color 0.15s;
            }
            .dp-trigger:hover {
                border-color: #86b7fe;
            }
            .dp-trigger.dp-error {
                border-color: #dc3545;
            }
            .dp-placeholder {
                color: #adb5bd;
            }
            .dp-clear {
                background: none;
                border: none;
                cursor: pointer;
                color: #adb5bd;
                margin-left: auto;
                font-size: 12px;
                padding: 0 2px;
                line-height: 1;
            }
            .dp-clear:hover {
                color: #dc3545;
            }
            .dp-popup {
                position: absolute;
                z-index: 1000;
                background: #fff;
                border: 1px solid #dee2e6;
                border-radius: 12px;
                padding: 16px;
                width: 280px;
                top: 100%;
                margin-top: 4px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
            }
            .dp-popup-up {
                top: auto;
                bottom: 100%;
                margin-top: 0;
                margin-bottom: 4px;
            }
            .cal-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 12px;
            }
            .cal-nav {
                background: none;
                border: none;
                cursor: pointer;
                color: #6c757d;
                font-size: 20px;
                padding: 2px 8px;
                border-radius: 6px;
                line-height: 1;
            }
            .cal-nav:hover {
                background: #f8f9fa;
            }
            .cal-month {
                font-size: 14px;
                font-weight: 600;
            }
            .cal-grid {
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                gap: 2px;
            }
            .cal-day-name {
                font-size: 11px;
                color: #adb5bd;
                text-align: center;
                padding: 4px 0;
            }
            .cal-day {
                font-size: 13px;
                text-align: center;
                padding: 6px 2px;
                border-radius: 6px;
                cursor: pointer;
                border: none;
                background: none;
                width: 100%;
                color: #212529;
            }
            .cal-day:hover:not(:disabled) {
                background: #f8f9fa;
            }
            .cal-day.cal-selected {
                background: #212529;
                color: #fff;
            }
            .cal-day.cal-today {
                font-weight: 700;
            }
            .cal-day:disabled {
                color: #dee2e6;
                cursor: not-allowed;
            }
            .cal-day.cal-other {
                color: #ced4da;
            }
            .time-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 8px;
                margin-top: 12px;
                padding-top: 12px;
                border-top: 1px solid #f0f0f0;
            }
            .time-field {
                display: flex;
                flex-direction: column;
                gap: 4px;
            }
            .time-label {
                font-size: 11px;
                color: #adb5bd;
            }
            .time-select {
                font-size: 13px;
                border: 1px solid #dee2e6;
                border-radius: 6px;
                padding: 6px 8px;
                width: 100%;
                cursor: pointer;
            }
            .dp-confirm {
                margin-top: 12px;
                width: 100%;
                padding: 8px;
                background: #212529;
                color: #fff;
                border: none;
                border-radius: 8px;
                font-size: 13px;
                cursor: pointer;
            }
            .dp-confirm:hover {
                background: #343a40;
            }
            .dp-field-error {
                font-size: 12px;
                color: #dc3545;
                margin-top: 4px;
            }
        `,
    ],
})
export class AuctionFormComponent implements OnInit, OnDestroy {
    request: CreateAuctionRequest = {
        title: '',
        description: '',
        basePrice: 0,
        startTime: '',
        endTime: '',
    };
    auction: Auction | null = null;
    loading = false;
    errorMessage = '';
    isEdit = false;
    auctionUid = '';

    // Picker state
    activePickerStart = false;
    activePickerEnd = false;
    startError = '';
    endError = '';

    startPopupUp = false;
    endPopupUp = false;

    readonly dayNames = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];
    readonly MONTHS = [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December',
    ];
    readonly hours = Array.from({ length: 24 }, (_, i) => i);
    readonly minutes = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];

    startViewYear = 0;
    startViewMonth = 0;
    endViewYear = 0;
    endViewMonth = 0;
    startSelDate: Date | null = null;
    endSelDate: Date | null = null;
    startHour = 0;
    startMinute = 0;
    endHour = 0;
    endMinute = 0;
    startCells: CalCell[] = [];
    endCells: CalCell[] = [];
    validStartHours: number[] = [];
    validStartMinutes: number[] = [];
    validEndHours: number[] = [];
    validEndMinutes: number[] = [];

    private clickListener = (e: MouseEvent) => this.onDocumentClick(e);

    constructor(
        private auctionService: AuctionService,
        private route: ActivatedRoute,
        private router: Router,
        private cdr: ChangeDetectorRef,
    ) {}

    ngOnInit(): void {
        const n = new Date();
        this.startViewYear = this.endViewYear = n.getFullYear();
        this.startViewMonth = this.endViewMonth = n.getMonth();
        this.startHour = this.endHour = n.getHours() + 1 > 23 ? 0 : n.getHours() + 1;
        this.startMinute = this.endMinute = 0;
        this.rebuildTimeOptions('start');
        this.rebuildTimeOptions('end');
        document.addEventListener('click', this.clickListener);

        const uid = this.route.snapshot.paramMap.get('uid');
        if (uid) {
            this.isEdit = true;
            this.auctionUid = uid;
            this.loadAuction();
        }
    }

    ngOnDestroy(): void {
        document.removeEventListener('click', this.clickListener);
    }

    onDocumentClick(e: MouseEvent): void {
        const target = e.target as HTMLElement;
        if (!target.closest('#start-container')) {
            this.activePickerStart = false;
        }
        if (!target.closest('#end-container')) {
            this.activePickerEnd = false;
        }
        this.cdr.detectChanges();
    }

    togglePicker(which: 'start' | 'end'): void {
        const container = document.getElementById(
            which === 'start' ? 'start-container' : 'end-container',
        );

        if (container) {
            const rect = container.getBoundingClientRect();

            const popupHeight = 450;
            const spaceBelow = window.innerHeight - rect.bottom;

            const shouldOpenUp = spaceBelow < popupHeight;

            if (which === 'start') {
                this.startPopupUp = shouldOpenUp;
            } else {
                this.endPopupUp = shouldOpenUp;
            }
        }

        if (which === 'start') {
            this.activePickerStart = !this.activePickerStart;
            this.activePickerEnd = false;

            if (this.activePickerStart) {
                this.buildCells('start');
                this.rebuildTimeOptions('start');
            }
        } else {
            this.activePickerEnd = !this.activePickerEnd;
            this.activePickerStart = false;

            if (this.activePickerEnd) {
                this.buildCells('end');
                this.rebuildTimeOptions('end');
            }
        }

        this.cdr.detectChanges();
    }

    changeMonth(which: 'start' | 'end', delta: number): void {
        if (which === 'start') {
            this.startViewMonth += delta;
            if (this.startViewMonth > 11) {
                this.startViewMonth = 0;
                this.startViewYear++;
            }
            if (this.startViewMonth < 0) {
                this.startViewMonth = 11;
                this.startViewYear--;
            }
            this.buildCells('start');
        } else {
            this.endViewMonth += delta;
            if (this.endViewMonth > 11) {
                this.endViewMonth = 0;
                this.endViewYear++;
            }
            if (this.endViewMonth < 0) {
                this.endViewMonth = 11;
                this.endViewYear--;
            }
            this.buildCells('end');
        }
    }

    getMonthLabel(which: 'start' | 'end'): string {
        return which === 'start'
            ? `${this.MONTHS[this.startViewMonth]} ${this.startViewYear}`
            : `${this.MONTHS[this.endViewMonth]} ${this.endViewYear}`;
    }

    buildCells(which: 'start' | 'end'): void {
        const year = which === 'start' ? this.startViewYear : this.endViewYear;
        const month = which === 'start' ? this.startViewMonth : this.endViewMonth;
        const selDate = which === 'start' ? this.startSelDate : this.endSelDate;
        const n = new Date();
        const today = new Date(n.getFullYear(), n.getMonth(), n.getDate());
        const startConfirmed = this.request.startTime ? new Date(this.request.startTime) : null;

        const cells: CalCell[] = [];
        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const prevDays = new Date(year, month, 0).getDate();

        for (let i = 0; i < firstDay; i++) {
            const d = prevDays - firstDay + 1 + i;
            cells.push({
                day: d,
                month: month - 1,
                year,
                selected: false,
                today: false,
                otherMonth: true,
                disabled: true,
            });
        }
        for (let d = 1; d <= daysInMonth; d++) {
            const cellDate = new Date(year, month, d);
            const isPast = cellDate < today;
            const isBeforeStart =
                which === 'end' && startConfirmed
                    ? cellDate <
                      new Date(
                          startConfirmed.getFullYear(),
                          startConfirmed.getMonth(),
                          startConfirmed.getDate(),
                      )
                    : false;
            cells.push({
                day: d,
                month,
                year,
                selected:
                    !!selDate &&
                    selDate.getDate() === d &&
                    selDate.getMonth() === month &&
                    selDate.getFullYear() === year,
                today: cellDate.getTime() === today.getTime(),
                otherMonth: false,
                disabled: isPast || isBeforeStart,
            });
        }
        if (which === 'start') this.startCells = cells;
        else this.endCells = cells;
    }

    selectDay(which: 'start' | 'end', cell: CalCell): void {
        if (cell.disabled) return;
        const d = new Date(cell.year, cell.month, cell.day);
        if (which === 'start') {
            this.startSelDate = d;
            this.buildCells('start');
        } else {
            this.endSelDate = d;
            this.buildCells('end');
        }
        this.rebuildTimeOptions(which);
    }

    rebuildTimeOptions(which: 'start' | 'end'): void {
        const n = new Date();
        const selDate = which === 'start' ? this.startSelDate : this.endSelDate;
        const curHour = which === 'start' ? this.startHour : this.endHour;
        const isToday = selDate && selDate.toDateString() === n.toDateString();
        const startConfirmed = this.request.startTime ? new Date(this.request.startTime) : null;
        const isSameAsStart =
            which === 'end' &&
            startConfirmed &&
            selDate &&
            selDate.toDateString() === startConfirmed.toDateString();

        // compute min hour
        const minHour = isToday ? n.getHours() : isSameAsStart ? startConfirmed!.getHours() : 0;

        // filtered hours — only valid options in the list
        const validHours = Array.from({ length: 24 }, (_, i) => i).filter((h) => h >= minHour);

        // compute min minute for current hour
        let minMinute = 0;
        if (isToday && curHour === n.getHours()) {
            minMinute = Math.ceil((n.getMinutes() + 1) / 5) * 5;
            if (minMinute >= 60) minMinute = 0;
        } else if (isSameAsStart && curHour === startConfirmed!.getHours()) {
            minMinute = Math.ceil((startConfirmed!.getMinutes() + 1) / 5) * 5;
            if (minMinute >= 60) minMinute = 0;
        }

        // filtered minutes — only valid options in the list
        const validMins = this.minutes.filter((m) => m >= minMinute);

        if (which === 'start') {
            this.validStartHours = validHours;
            this.validStartMinutes = validMins;
            // clamp selected values to valid ranges
            if (!validHours.includes(this.startHour)) this.startHour = validHours[0] ?? 0;
            if (!validMins.includes(this.startMinute)) this.startMinute = validMins[0] ?? 0;
        } else {
            this.validEndHours = validHours;
            this.validEndMinutes = validMins;
            if (!validHours.includes(this.endHour)) this.endHour = validHours[0] ?? 0;
            if (!validMins.includes(this.endMinute)) this.endMinute = validMins[0] ?? 0;
        }
    }

    onHourChange(which: 'start' | 'end'): void {
        // [(ngModel)] may not have updated yet — read from event directly
        const selectEl = document.getElementById(
            which === 'start' ? 'start-hour-select' : 'end-hour-select',
        ) as HTMLSelectElement;

        if (selectEl) {
            const selectedHour = parseInt(selectEl.value);
            if (which === 'start') this.startHour = selectedHour;
            else this.endHour = selectedHour;
        }

        this.rebuildTimeOptions(which);
        this.cdr.detectChanges();
    }

    confirmPicker(which: 'start' | 'end'): void {
        const selDate = which === 'start' ? this.startSelDate : this.endSelDate;
        const hour = which === 'start' ? this.startHour : this.endHour;
        const minute = which === 'start' ? this.startMinute : this.endMinute;

        if (!selDate) {
            if (which === 'start') this.startError = 'Please select a date';
            else this.endError = 'Please select a date';
            return;
        }

        const dt = new Date(selDate);
        dt.setHours(hour, minute, 0, 0);

        if (which === 'start') {
            if (dt <= new Date()) {
                this.startError = 'Start time must be in the future';
                return;
            }
            if (this.request.endTime && dt >= new Date(this.request.endTime)) {
                this.startError = 'Start time must be before end time';
                return;
            }
            this.request.startTime = dt.toISOString();
            this.startError = '';
            this.activePickerStart = false;
            // reset end if it's now before start
            if (this.request.endTime && new Date(this.request.endTime) <= dt) {
                this.request.endTime = '';
                this.endSelDate = null;
            }
        } else {
            if (!this.request.startTime) {
                this.endError = 'Please select start time first';
                return;
            }
            if (dt <= new Date(this.request.startTime)) {
                this.endError = 'End time must be after start time';
                return;
            }
            this.request.endTime = dt.toISOString();
            this.endError = '';
            this.activePickerEnd = false;
        }
        this.cdr.detectChanges();
    }

    clearField(e: Event, which: 'start' | 'end'): void {
        e.stopPropagation();
        if (which === 'start') {
            this.request.startTime = '';
            this.startSelDate = null;
            this.startError = '';
        } else {
            this.request.endTime = '';
            this.endSelDate = null;
            this.endError = '';
        }
    }

    formatDisplay(iso: string): string {
        const d = new Date(iso);
        return (
            d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) +
            '  ' +
            d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true })
        );
    }

    padTwo(n: number): string {
        return n.toString().padStart(2, '0');
    }

    loadAuction(): void {
        this.loading = true;
        this.auctionService.getAuctionById(this.auctionUid).subscribe({
            next: (auction) => {
                this.auction = auction;
                this.request = {
                    title: auction.title,
                    description: auction.description,
                    basePrice: auction.basePrice,
                    startTime: auction.startTime,
                    endTime: auction.endTime,
                };
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.loading = false;
                this.errorMessage = 'Unable to load auction';
                this.cdr.detectChanges();
            },
        });
    }

    submit(): void {
        this.errorMessage = '';
        if (!this.request.title.trim() || this.request.title.trim().length < 5) {
            this.errorMessage = 'Title must be at least 5 characters';
            return;
        }
        if (!this.request.description.trim() || this.request.description.trim().length < 10) {
            this.errorMessage = 'Description must be at least 10 characters';
            return;
        }
        if (this.request.basePrice <= 0) {
            this.errorMessage = 'Base price must be greater than 0';
            return;
        }
        if (!this.request.startTime) {
            this.startError = 'Start time is required';
            return;
        }
        if (!this.request.endTime) {
            this.endError = 'End time is required';
            return;
        }
        this.isEdit ? this.updateAuction() : this.createAuction();
    }

    createAuction(): void {
        this.loading = true;
        this.auctionService.createAuction(this.request).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(['/my-auctions']);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = err.error?.message ?? 'Failed to create auction';
                this.cdr.detectChanges();
            },
        });
    }

    updateAuction(): void {
        this.loading = true;
        const req: UpdateAuctionRequest = {
            uid: this.auctionUid,
            title: this.request.title,
            description: this.request.description,
            basePrice: this.request.basePrice,
            startTime: this.request.startTime,
            endTime: this.request.endTime,
        };
        this.auctionService.updateAuction(req).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(['/my-auctions']);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = err.error?.message ?? 'Failed to update auction';
                this.cdr.detectChanges();
            },
        });
    }
}
