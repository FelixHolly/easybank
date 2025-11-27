import {Component, inject} from '@angular/core';
import {RouterModule} from '@angular/router';
import {LoggerService} from './core';

@Component({
  imports: [RouterModule],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected title = 'easybank';
  private readonly logger = inject(LoggerService);

  constructor() {
    this.logger.init('EasyBank', '1.0.0');
  }
}
