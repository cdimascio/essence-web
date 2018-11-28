import React, { Component } from 'react';
import essence from './essence.png';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.onUrlClick = this.onUrlClick.bind(this);
    this.onTextChange = this.onTextChange.bind(this);
    this.onEnter = this.onEnter.bind(this);
    this.renderResponse = this.renderResponse.bind(this);
    this.state = {
      url: '',
      result: null,
    };
  }

  render() {
    return (
      <div className="App">
        <div>
          <img src={essence} class="essence" />
        </div>
        <div className="search">
          <label>enter a url:</label>
          <div class="search">
            <input
              type="text"
              name="url"
              placeholder="enter a url"
              className="search-box"
              value={this.state.url}
              onChange={this.onTextChange}
              onKeyPress={this.onEnter}
            />
            {this.state.error ? this.renderError() : this.renderResponse()}
          </div>
        </div>
      </div>
    );
  }

  onEnter(e) {
    if (e.key === 'Enter') {
      this.onUrlClick(e);
    }
  }

  onTextChange(e) {
    this.setState({
      url: e.target.value,
    });
  }
  onUrlClick(e) {
    // console.log(`http://localhost:8080/extract?url=${this.state.url}`);
    this.setState({
      error: null,
    });
    fetch(`/extract?url=${this.state.url}`)
      .then(r => {
        if (r.status !== 200) {
          console.log(r);
          throw Error(r.statusText);
        }
        return r.json();
      })
      .then(r => {
        console.log(r);
        this.setState({
          result: r,
        });
      })
      .catch(e => {
        this.setState({
          error: e,
        });
      });
    e.preventDefault();
  }
  renderResponse() {
    return !this.state.result ? null : (
      <div className="extractor-result">
        <pre>{JSON.stringify(this.state.result, null, '  ')}</pre>
      </div>
    );
  }

  renderError() {
    return !this.state.error ? null : (
      <div className="error">{`Could not load url "${this.state.url}"`}</div>
    );
  }
}

export default App;
