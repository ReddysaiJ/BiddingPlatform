import { Component, Input, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuctionService } from '../../core/services/auction.service';
import { forkJoin, from, switchMap } from 'rxjs';
import { SignedUploadResponse } from '../../core/models/signedUploadResponse.model';

@Component({
  selector: 'app-auction-image-manager',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './auction-image-manager.component.html'
})
export class AuctionImageManagerComponent implements OnInit {
  @Input() auctionUid!: string;

  images: string[] = [];
  selectedFiles: File[] = [];
  previews: string[] = [];
  uploading = false;
  uploadError = '';
  uploadSuccess = false;

  constructor(
    private auctionService: AuctionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.auctionService.getImagesByUid(this.auctionUid).subscribe({
      next: (res) => {
        this.images = res.imageUrls ?? [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.images = [];
        this.cdr.markForCheck();
      }
    });
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    this.selectedFiles = Array.from(input.files);
    this.previews = [];
    this.uploadError = '';
    this.uploadSuccess = false;

    // Generate previews
    this.selectedFiles.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previews.push(e.target?.result as string);
        this.cdr.markForCheck();
      };
      reader.readAsDataURL(file);
    });
  }

  uploadImages(): void {
    if (!this.selectedFiles.length) return;

    this.uploading = true;
    this.uploadError = '';
    this.uploadSuccess = false;

    this.auctionService.getExtraImageSignatures(this.auctionUid, this.selectedFiles.length)
      .pipe(
        switchMap((signedUrls: SignedUploadResponse[]) => {
          const uploads = this.selectedFiles.map((file, i) =>
            this.auctionService.uploadToCloudinary(file, signedUrls[i])
          );
          return forkJoin(uploads).pipe(
            switchMap(() => {
              const imageUrls = signedUrls.map(s =>
                `https://res.cloudinary.com/${s.cloudName}/image/upload/${s.publicId}`
              );
              const publicIds = signedUrls.map(s => s.publicId);
              return this.auctionService.saveImageUrls(this.auctionUid, imageUrls, publicIds);
            })
          );
        })
      )
      .subscribe({
        next: () => {
          this.uploading = false;
          this.uploadSuccess = true;
          this.selectedFiles = [];
          this.previews = [];
          this.loadImages();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.uploading = false;
          this.uploadError = 'Upload failed. Please try again.';
          console.error(err);
          this.cdr.markForCheck();
        }
      });
  }
}
