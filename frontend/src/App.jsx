import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Search, Play, Music, ListMusic, Plus, PlayCircle, Library, PlusCircle, Heart, SkipForward } from 'lucide-react';

import './index.css';

const API_GATEWAY = 'http://localhost:8080';

export default function App() {
  const [query, setQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [playlists, setPlaylists] = useState([]);
  const [newPlaylistName, setNewPlaylistName] = useState('');
  const [loading, setLoading] = useState(false);
  const [nowPlaying, setNowPlaying] = useState(null);
  const audioRef = React.useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  
  const [viewingPlaylist, setViewingPlaylist] = useState(null);
  const [playlistSongs, setPlaylistSongs] = useState([]);
  const [showAddToPlaylistFor, setShowAddToPlaylistFor] = useState(null);
  const [showHeartMenu, setShowHeartMenu] = useState(false);
  const [heartPlaylistName, setHeartPlaylistName] = useState('');

  useEffect(() => {
    fetchRecommendations();
    fetchPlaylists();
  }, []);

  const fetchRecommendations = async () => {
    try {
      const { data: topIds } = await axios.get(`${API_GATEWAY}/recommendation/top?limit=4`);
      const enrichedRecs = await Promise.all(
        topIds.map(async (id) => {
           try {
              const res = await axios.get(`${API_GATEWAY}/music/search?q=${id}`);
              if(res.data && res.data.length > 0) return res.data[0];
              return { id, title: `Track ${id}`, uploader: 'Unknown', thumbnailUrl: '' };
           } catch(e) {
              return { id, title: `Track ${id}`, uploader: 'Unknown', thumbnailUrl: '' };
           }
        })
      );
      setRecommendations(enrichedRecs);
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    }
  };

  const fetchPlaylists = async () => {
    try {
      const { data } = await axios.get(`${API_GATEWAY}/playlist`);
      setPlaylists(data);
    } catch (error) {
      console.error('Error fetching playlists:', error);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    setViewingPlaylist(null); 
    try {
      const { data } = await axios.get(`${API_GATEWAY}/music/search?q=${query}`);
      setSearchResults(data);
    } catch (error) {
      console.error('Error searching:', error);
    }
    setLoading(false);
  };

  const createPlaylist = async (e) => {
    e.preventDefault();
    if (!newPlaylistName.trim()) return;
    try {
      await axios.post(`${API_GATEWAY}/playlist`, {
        name: newPlaylistName,
        songIds: []
      });
      setNewPlaylistName('');
      fetchPlaylists();
    } catch (error) {
      console.error('Error creating playlist:', error);
    }
  };

  const createAndAddToPlaylist = async (e) => {
    e.preventDefault();
    if (!heartPlaylistName.trim() || !nowPlaying) return;
    try {
      const { data } = await axios.post(`${API_GATEWAY}/playlist`, {
        name: heartPlaylistName,
        songIds: []
      });
      await addToPlaylist(data.id, nowPlaying.id);
      setHeartPlaylistName('');
      setShowHeartMenu(false);
      fetchPlaylists();
    } catch (error) {
      console.error('Error creating playlist and adding song:', error);
    }
  };

  const playSong = async (song) => {
    const prevId = nowPlaying ? nowPlaying.id : '';
    setNowPlaying(song);
    setIsPlaying(false);
    setProgress(0);
    if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.src = '';
    }
    try {
      const url = `${API_GATEWAY}/stream/${song.id}${prevId ? `?previousVideoId=${prevId}` : ''}`;
      const { data } = await axios.get(url);
      if (data.streamUrl && audioRef.current) {
         audioRef.current.src = data.streamUrl;
         audioRef.current.play();
         setIsPlaying(true);
      }
      setTimeout(fetchRecommendations, 800); 
    } catch (error) {
      console.error('Error playing song:', error);
    }
  };

  const playNextRecommendation = async () => {
    if (!nowPlaying) return;
    try {
      const { data: nextId } = await axios.get(`${API_GATEWAY}/recommendation/next/${nowPlaying.id}`);
      if (nextId) {
        // Fetch song details and play
        const res = await axios.get(`${API_GATEWAY}/music/search?q=${nextId}`);
        if (res.data && res.data.length > 0) {
          playSong(res.data[0]);
        }
      } else if (recommendations.length > 0) {
        // Fallback to first available recommendation
        playSong(recommendations[0]);
      }
    } catch (error) {
      console.error('Error fetching next recommendation:', error);
    }
  };


  const togglePlay = () => {
    if (!audioRef.current || !nowPlaying) return;
    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      audioRef.current.play();
      setIsPlaying(true);
    }
  };

  const handleTimeUpdate = () => {
    if (audioRef.current) {
      const { currentTime, duration } = audioRef.current;
      if (duration) {
        setProgress((currentTime / duration) * 100);
      }
    }
  };

  const handleSeek = (e) => {
    if (audioRef.current) {
      const bounds = e.currentTarget.getBoundingClientRect();
      const percent = (e.clientX - bounds.left) / bounds.width;
      audioRef.current.currentTime = percent * audioRef.current.duration;
    }
  };

  const addToPlaylist = async (playlistId, songId) => {
    try {
      await axios.post(`${API_GATEWAY}/playlist/${playlistId}/songs/${songId}`);
      setShowAddToPlaylistFor(null);
      if (viewingPlaylist && viewingPlaylist.id === playlistId) {
         openPlaylist(viewingPlaylist);
      }
    } catch(e) {
      console.error("Error adding to playlist", e);
    }
  };

  const openPlaylist = async (playlist) => {
     setViewingPlaylist(playlist);
     setLoading(true);
     try {
        const { data } = await axios.get(`${API_GATEWAY}/playlist/${playlist.id}`);
        if (data.songsIds && data.songsIds.length > 0) {
            const enriched = await Promise.all(
               data.songsIds.map(async (id) => {
                  try {
                    const res = await axios.get(`${API_GATEWAY}/music/search?q=${id}`);
                    if(res.data && res.data.length > 0) return res.data[0];
                    return { id, title: `Track ${id}`, uploader: 'Unknown', thumbnailUrl: '' };
                  } catch(e) {
                    return { id, title: `Track ${id}`, uploader: 'Unknown', thumbnailUrl: '' };
                  }
               })
            );
            setPlaylistSongs(enriched);
        } else {
            setPlaylistSongs([]);
        }
     } catch(e) {
        console.error("Error fetching playlist", e);
     }
     setLoading(false);
  };

  const renderCard = (song) => (
    <div className="song-card" key={song.id}>
      <div onClick={() => playSong(song)}>
        <div style={{overflow: 'hidden', borderBottom: '4px solid #000'}}>
          <img src={song.thumbnailUrl} alt={song.title} className="song-thumbnail" />
        </div>
        <div className="song-info" style={{padding: '10px 0'}}>
          <span className="song-title">{song.title}</span>
          <span className="song-uploader">BY {song.uploader}</span>
        </div>
      </div>
      
      <button 
         style={{ position: 'absolute', top: '-15px', right: '-15px', background: 'var(--neo-yellow)', border: '4px solid #000', color: '#000', padding: '10px', cursor: 'pointer', zIndex: 20, boxShadow: '4px 4px 0px #000', borderRadius: '50%', transition: 'all 0.1s' }}
         onClick={(e) => { e.stopPropagation(); setShowAddToPlaylistFor(showAddToPlaylistFor === song.id ? null : song.id); }}
         onMouseDown={e => { e.currentTarget.style.transform = 'translate(4px, 4px)'; e.currentTarget.style.boxShadow = '0px 0px 0px #000'; }}
         onMouseUp={e => { e.currentTarget.style.transform = 'translate(0px, 0px)'; e.currentTarget.style.boxShadow = '4px 4px 0px #000'; }}
      >
         <Plus size={24} strokeWidth={4} />
      </button>

      {showAddToPlaylistFor === song.id && (
         <div className="pixel-dropdown">
            <div style={{ marginBottom: '15px', borderBottom: '4px solid #000', paddingBottom: '10px', fontWeight: 900, fontSize: '18px' }}>ADD TO PLAYLIST</div>
            {playlists.length > 0 ? playlists.map(pl => (
               <div key={pl.id} className="pixel-dropdown-item" onClick={() => addToPlaylist(pl.id, song.id)}>
                 {pl.name}
               </div>
            )) : <div style={{fontWeight: 700}}>NO PLAYLISTS YET</div>}
         </div>
      )}
    </div>
  );

  return (
    <div className="layout" onClick={() => setShowAddToPlaylistFor(null)}>
      <header className="header">
        <div className="logo" style={{cursor:'pointer'}} onClick={() => {setViewingPlaylist(null); setSearchResults([])}}>
          <Music size={40} strokeWidth={4} color="#000" />
          <h1 className="glitch-text" data-text="HARMONY NEO">HARMONY NEO</h1>
        </div>
      </header>

      <main className="main-content">
        <div className="panel panel-yellow">
          <h2 style={{ fontSize: '36px', marginBottom: '20px', background: '#000', color: '#fff', display: 'inline-block', padding: '10px 20px', transform: 'rotate(1deg)' }}>DROP THE BEAT NOW</h2>
          <form className="search-container" onSubmit={handleSearch}>
            <Search className="search-icon-absolute" size={30} strokeWidth={3} />
            <input 
              type="text" 
              className="search-input" 
              placeholder="SEARCH FOR BANGER TRACKS..." 
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </form>
        </div>

        <div className="row">
          <div className="col" style={{ flex: 2 }}>
            <div className="panel panel-pink">
              <div className="section-title" style={{transform: 'rotate(1deg)', background: '#fff', color: '#000', boxShadow: '4px 4px 0px #000'}}>
                <ListMusic size={28} />
                {viewingPlaylist ? `PLAYLIST: ${viewingPlaylist.name}` : 'SEARCH RESULTS'}
              </div>
              
              {loading ? (
                <div style={{ textAlign: 'center', padding: '60px', fontWeight: 900, fontSize: 24 }}>LOADING AWESOMENESS...</div>
              ) : viewingPlaylist ? (
                 playlistSongs.length > 0 ? (
                    <div className="card-grid">
                      {playlistSongs.map(renderCard)}
                    </div>
                 ) : (
                    <div style={{ textAlign: 'center', padding: '60px', fontWeight: 900, fontSize: 24 }}>PLAYLIST IS EMPTY.</div>
                 )
              ) : searchResults.length > 0 ? (
                <div className="card-grid">
                  {searchResults.map(renderCard)}
                </div>
              ) : (
                <div style={{ textAlign: 'center', padding: '60px', fontWeight: 900, fontSize: 24, textTransform: 'uppercase' }}>
                  SEARCH AROUND FOR MUSIC. NOW.
                </div>
              )}
            </div>
          </div>

          <div className="col">
            <div className="panel panel-blue">
              <div className="section-title">
                <PlayCircle size={28} />
                HOT RANKINGS
              </div>
              <div style={{ display: 'flex', flexDirection: 'column' }}>
                {recommendations.length > 0 ? recommendations.map(song => (
                  <div key={song.id} className="pixel-list-item" onClick={() => playSong(song)}>
                     <img src={song.thumbnailUrl || 'https://via.placeholder.com/60?text=Song'} alt={song.title} />
                     <div style={{ flex: 1, minWidth: 0 }}>
                       <div className="song-title">{song.title}</div>
                       <div className="song-uploader">BY {song.uploader}</div>
                     </div>
                  </div>
                )) : (
                  <div style={{ fontWeight: 900, padding: 20 }}>NO PLAY DATA YET.</div>
                )}
              </div>
            </div>

            <div className="panel panel-green">
              <div className="section-title">
                <Library size={28} />
                YOUR MIXTAPES
              </div>
              
              <div style={{ marginBottom: '30px' }}>
                {playlists.length > 0 ? playlists.map(pl => (
                  <div key={pl.id} className="pixel-list-item" onClick={() => openPlaylist(pl)}>
                    <div style={{flex: 1}}>
                       <div className="song-title">{pl.name}</div>
                       <div className="song-uploader">{pl.songsIds?.length || 0} TRACKS INSIDE</div>
                    </div>
                  </div>
                )) : (
                  <div style={{ fontWeight: 900, padding: 20 }}>NO MIXTAPES CREATED.</div>
                )}
              </div>

              <form onSubmit={createPlaylist} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <input 
                  type="text" 
                  className="search-input" 
                  style={{padding: '15px'}}
                  placeholder="NEW MIXTAPE NAME"
                  value={newPlaylistName} 
                  onChange={e => setNewPlaylistName(e.target.value)} 
                  required 
                />
                <button type="submit" className="pixel-btn">
                  <Plus size={24} strokeWidth={3} /> DROP IT
                </button>
              </form>
            </div>
          </div>
        </div>
      </main>

      <div className={`now-playing-bar ${nowPlaying ? 'active' : ''}`}>
        <div className="np-info">
          {nowPlaying && (
             <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
               <span className="np-title">{nowPlaying.title}</span>
               <span style={{fontSize: '12px', fontWeight: 400}}>{nowPlaying.uploader}</span>
             </div>
          )}
        </div>
        
        <div className="np-controls">
          <div style={{display: 'flex', gap: '30px', alignItems: 'center'}}>
             <button className="pixel-btn" onClick={togglePlay} style={{background: '#000', color: '#fff', border: '4px solid #fff'}}>
               {isPlaying ? <strong>|| FREEZE</strong> : <strong>► BLAST IT</strong>}
             </button>
             <button className="pixel-btn" onClick={playNextRecommendation} style={{background: 'var(--neo-green)', color: '#000', border: '4px solid #000'}} title="PLAY NEXT RECOMMENDATION">
                <SkipForward size={24} strokeWidth={4} />
             </button>
          </div>
          <div className="progress-bar-container" onClick={handleSeek}>
             <div className="progress-bar-fill" style={{ width: `${progress}%` }}></div>
          </div>
        </div>
        
        <div style={{ width: '30%', display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: '20px', position: 'relative' }}>
          
          <button onClick={() => setShowHeartMenu(!showHeartMenu)} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: showHeartMenu ? 'var(--neo-pink)' : '#fff', transition: 'transform 0.1s' }} onMouseDown={e => e.currentTarget.style.transform='scale(0.9)'} onMouseUp={e => e.currentTarget.style.transform='scale(1)'}>
             <Heart size={36} strokeWidth={3} fill={showHeartMenu ? 'var(--neo-pink)' : 'transparent'} />
          </button>

          {showHeartMenu && nowPlaying && (
             <div className="pixel-dropdown" style={{ bottom: '100px', right: '10px', top: 'auto', background: 'var(--neo-yellow)' }}>
                <div style={{ marginBottom: '15px', borderBottom: '4px solid #000', paddingBottom: '10px', fontWeight: 900, fontSize: '18px', color: '#000' }}>SAVE TO...</div>
                <div style={{maxHeight: '150px', overflowY: 'auto', marginBottom: '10px', color: '#000'}}>
                  {playlists.length > 0 ? playlists.map(pl => (
                    <div key={pl.id} className="pixel-dropdown-item" onClick={() => {addToPlaylist(pl.id, nowPlaying.id); setShowHeartMenu(false);}}>
                      + {pl.name}
                    </div>
                  )) : <div style={{fontWeight: 700}}>NO MIXTAPES YET</div>}
                </div>
                <form onSubmit={createAndAddToPlaylist} style={{ display: 'flex', flexDirection: 'column', gap: '8px', borderTop: '4px solid #000', paddingTop: '10px' }}>
                  <input type="text" className="search-input" style={{padding: '10px', fontSize: '12px'}} placeholder="NEW MIXTAPE..." value={heartPlaylistName} onChange={e => setHeartPlaylistName(e.target.value)} required />
                  <button type="submit" className="pixel-btn" style={{padding: '10px', fontSize: '12px'}}>CREATE & SAVE</button>
                </form>
             </div>
          )}

          <div style={{ padding: '10px 20px', background: '#000', color: 'var(--neo-green)', fontWeight: 900, border: '4px solid #fff', fontSize: '24px' }}>
            {isPlaying ? 'ON AIR' : 'MUTED'}
          </div>
        </div>
        <audio ref={audioRef} onEnded={playNextRecommendation} onTimeUpdate={handleTimeUpdate} />
      </div>

    </div>
  );
}
