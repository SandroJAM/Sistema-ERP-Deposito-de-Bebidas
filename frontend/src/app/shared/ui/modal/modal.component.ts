import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  template: `
    <div class="modal-fundo" (click)="fechar.emit()">
      <div class="modal-caixa" (click)="$event.stopPropagation()">
        <header class="modal-cabecalho">
          <h2>{{ titulo }}</h2>
          <button type="button" class="modal-fechar" (click)="fechar.emit()" aria-label="Fechar">×</button>
        </header>
        <div class="modal-corpo">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-fundo {
      position: fixed;
      inset: 0;
      background: rgba(18, 24, 31, 0.45);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1rem;
      z-index: 100;
    }
    .modal-caixa {
      width: 100%;
      max-width: 420px;
      background: var(--cor-superficie);
      border-radius: var(--raio);
      box-shadow: var(--sombra);
      max-height: 90vh;
      overflow-y: auto;
    }
    .modal-cabecalho {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1.1rem 1.25rem;
      border-bottom: 1px solid var(--cor-borda);

      h2 { font-size: 1.05rem; margin: 0; }
    }
    .modal-fechar {
      background: none;
      border: none;
      font-size: 1.4rem;
      line-height: 1;
      color: var(--cor-texto-suave);
      padding: 0.2rem 0.4rem;

      &:hover { color: var(--cor-texto); }
    }
    .modal-corpo {
      padding: 1.25rem;
    }
  `],
})
export class ModalComponent {
  @Input() titulo = '';
  @Output() fechar = new EventEmitter<void>();
}
