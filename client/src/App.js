import React, { Component } from 'react';
import logo from './logo.svg';
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
        <div className="search">
          {/* <form> */}
          <label>url:</label>
          <div>
            <input
              type="text"
              name="url"
              className="search-box"
              value={this.state.url}
              onChange={this.onTextChange}
              onKeyPress={this.onEnter}
            />
            {/* <input
              type="submit"
              className="search-button"
              value="Extract"
              onClick={this.onUrlClick}
            /> */}
            {this.renderResponse()}
          </div>
          {/* </form> */}
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
    console.log(`http://localhost:8080/extract?url=${this.state.url}`);
    fetch(`http://localhost:8080/extract?url=${this.state.url}`)
      .then(r => r.json())
      .then(r => {
        console.log(r);
        this.setState({
          result: r,
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
}

export default App;
